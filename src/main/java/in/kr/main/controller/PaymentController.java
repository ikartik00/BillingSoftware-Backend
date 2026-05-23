package in.kr.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;

import in.kr.main.io.OrderResponse;
import in.kr.main.io.PaymentVerificationRequest;
import in.kr.main.io.RazorpayOrderResponse;
import in.kr.main.io.RazorpayRequest;
import in.kr.main.service.OrderService;
import in.kr.main.service.RazorpayService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
	private final RazorpayService razorpayService;
	private final OrderService orderService;
	
	@PostMapping("/create-order")
	public ResponseEntity<RazorpayOrderResponse> createRazorpayOrder(@RequestBody RazorpayRequest request) throws RazorpayException {
		return new ResponseEntity<RazorpayOrderResponse>( razorpayService.createOrder(request.getAmount(), request.getCurrency()),HttpStatus.CREATED);
	}
	
	@PostMapping("/verify")
	public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
		return orderService.verifyPayment(request);
	}
}
