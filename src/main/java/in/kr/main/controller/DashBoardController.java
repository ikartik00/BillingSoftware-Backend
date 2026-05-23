package in.kr.main.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.io.DashBoardResponse;
import in.kr.main.io.OrderResponse;
import in.kr.main.service.OrderService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashBoardController {
	private final OrderService orderService;
	
	@GetMapping("/")
	public DashBoardResponse getDashboardData() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		LocalDate today = LocalDate.now();
		Double totalSale = orderService.sumSalesByDate(today, email);
		Long totalOrders = orderService.countByOrderDate(today, email);
		List<OrderResponse> recentOrders = orderService.findRecentOrders(email);
		return new DashBoardResponse(totalSale != null ? totalSale : 0.0, totalOrders!= null ? totalOrders : 0, recentOrders);
	}
}
