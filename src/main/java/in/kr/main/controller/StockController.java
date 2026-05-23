package in.kr.main.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.io.ItemResponse;
import in.kr.main.io.StockRequest;
import in.kr.main.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class StockController {
	private final StockService stockService;
	
	@PostMapping("/add-stock")
	public ResponseEntity<ItemResponse> addStock(@Valid @RequestBody StockRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		ItemResponse response = stockService.addStock(request, email);
		return new ResponseEntity<ItemResponse>(response, HttpStatus.OK);
	}
}
