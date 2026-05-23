package in.kr.main.io;

import java.time.LocalDateTime;
import java.util.List;

import in.kr.main.entity.PaymentDetails;
import in.kr.main.enums.PaymentMethod;
import in.kr.main.io.OrderRequest.OrderItemRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
	private String orderId;
	private String customerName;
	private String mobileNumber;
	private List<OrderResponse.OrderItemResponse> cartItems;
	private Double subtotal;
	private Double tax;
	private Double grandTotal;
	private PaymentMethod paymentMethod;
	private LocalDateTime createdAt;
	private PaymentDetails paymentDetails;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class OrderItemResponse{
		private String itemId;
		private String name;
		private Double price;
		private Integer quantity;
	}
}
