package in.kr.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.io.OrderRequest;
import in.kr.main.io.OrderResponse;
import in.kr.main.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	
	@PostMapping("/create-order")
	public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		OrderResponse response = orderService.createOrder(orderRequest, email);
		return new ResponseEntity<OrderResponse>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/delete/{orderId}")
	public ResponseEntity<?> deleteOrderByOrderId(@PathVariable String orderId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		orderService.deleteByOrderId(orderId, email);
		return new ResponseEntity<String>("Deleted", HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/all-orders")
	public ResponseEntity<List<OrderResponse>> getLatestOrders(@RequestParam int page, @RequestParam int size){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		List<OrderResponse> latestOrders = orderService.getLatestOrders(email, page,size);
		return new ResponseEntity<List<OrderResponse>>(latestOrders, HttpStatus.OK);
	}
}
