package in.kr.main.service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.ItemsEntity;
import in.kr.main.entity.OrderEntity;
import in.kr.main.entity.OrderItemsEntity;
import in.kr.main.entity.PaymentDetails;
import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.StockEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.enums.PaymentMethod;
import in.kr.main.exceptions.ItemNotExistException;
import in.kr.main.io.OrderRequest;
import in.kr.main.io.OrderResponse;
import in.kr.main.io.PaymentVerificationRequest;
import in.kr.main.repository.ItemsRepository;
import in.kr.main.repository.OrderRepository;
import in.kr.main.repository.StockRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ItemsRepository itemRepository;
	private final StockRepository stockRepository;
	
	@Transactional
	public OrderResponse createOrder(OrderRequest request, String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		OrderEntity newOrder = convertToOrderEntity(request);
		PaymentDetails paymentDetails = new PaymentDetails();
		paymentDetails.setStatus(newOrder.getPaymentMethod() == PaymentMethod.CASH ? PaymentDetails.PaymentStatus.COMPLETED : PaymentDetails.PaymentStatus.PENDING);
		newOrder.setPaymentDetails(paymentDetails);
		
		List<OrderItemsEntity> orderItems = request.getCartItems().stream()
				.map(this::convertToOrderItemEntity)
				.collect(Collectors.toList());
		orderItems.forEach(item->item.setOrder(newOrder));
		newOrder.setOrderItems(orderItems);
		newOrder.setShop(shop);
		OrderEntity savedOrder = orderRepository.save(newOrder);
		return convertToResponse(savedOrder);
	}
	
	private OrderItemsEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
		ItemsEntity itemEntity = itemRepository.findByItemId(orderItemRequest.getItemId()).orElseThrow(()->new ItemNotExistException("Item Not exist with this id " + orderItemRequest.getItemId()));
		StockEntity existingStockEntity = stockRepository.findByItem(itemEntity);
		if(existingStockEntity.getAvailableStock() < orderItemRequest.getQuantity()) {
			throw new RuntimeException("Stock is not available");
		}
		existingStockEntity.setAvailableStock(existingStockEntity.getAvailableStock() - orderItemRequest.getQuantity());
		stockRepository.save(existingStockEntity);		
	
		return OrderItemsEntity.builder()
			.itemId(orderItemRequest.getItemId())
			.name(itemEntity.getName())
			.price(itemEntity.getPrice())
			.quantity(orderItemRequest.getQuantity())
			.build();
	}
	
	private OrderResponse convertToResponse(OrderEntity newOrder) {
			return OrderResponse.builder()
				.customerName(newOrder.getCustomerName())
				.mobileNumber(newOrder.getMobileNumber())
				.tax(newOrder.getTax())
				.subtotal(newOrder.getSubTotal())
				.grandTotal(newOrder.getGrandTotal())
				.createdAt(newOrder.getCreatedAt())
				.paymentMethod(newOrder.getPaymentMethod())
				.paymentDetails(newOrder.getPaymentDetails())
				.orderId(newOrder.getOrderId())
				.cartItems(newOrder.getOrderItems().stream().map(this::convertToItemResponse).collect(Collectors.toList()))
				.build();
	}
	
	private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemsEntity orderItemsEntity) {
		return OrderResponse.OrderItemResponse.builder()
			.itemId(orderItemsEntity.getItemId())
			.name(orderItemsEntity.getName())
			.price(orderItemsEntity.getPrice())
			.quantity(orderItemsEntity.getQuantity())
			.build();
	}
	
	private OrderEntity convertToOrderEntity(OrderRequest request) {
		return OrderEntity.builder()
		.customerName(request.getCustomerName())
		.mobileNumber(request.getMobileNumber())
		.subTotal(request.getSubtotal())
		.tax(request.getTax())
		.grandTotal(request.getGrandTotal())
		.paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
		.build();
	}
	
	public void deleteByOrderId(String orderId, String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		OrderEntity orderEntity = orderRepository.findByOrderIdAndShop(orderId, shop).orElseThrow(()-> new UsernameNotFoundException("Order Doens't exists with this shop and orderid " + orderId));
		orderRepository.delete(orderEntity);
	}
	
	public List<OrderResponse> getLatestOrders(String email){
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		List<OrderEntity> latestOrders = orderRepository.findAllByShopOrderByCreatedAtDesc(shop);
		return latestOrders.stream().map(this::convertToResponse).collect(Collectors.toList());
	}

	public OrderResponse verifyPayment(PaymentVerificationRequest request) {
		OrderEntity existingOrder = orderRepository.findByOrderId(request.getOrderId()).orElseThrow(()-> new RuntimeException("Order not found with this id" + request.getOrderId()));
		if(!verifyRazorpaySignature(request.getRazorpayOrderId(),request.getRazorpayPaymentId(),request.getRazorpaySignature())){
			throw new RuntimeException("Payment Verification Failed");
		}
		PaymentDetails paymentDetails = existingOrder.getPaymentDetails();
		paymentDetails.setRazorpayOrderId(request.getRazorpayOrderId());
		paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
		paymentDetails.setRazorpaySignature(request.getRazorpaySignature());
		paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);
		
		orderRepository.save(existingOrder);
		return convertToResponse(existingOrder);
	}

	private boolean verifyRazorpaySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
		return true;
	}
	
	public Double sumSalesByDate(LocalDate date, String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		return orderRepository.sumSalesByDate(date, shop);
	}
	
	public Long countByOrderDate(LocalDate date, String email) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		return orderRepository.countByOrderDate(date, shop);
	}
	
	public List<OrderResponse> findRecentOrders(String email){
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not exists with this email" + email));
		ShopEntity shop = userEntity.getShop();
		return orderRepository.findRecentOrders(PageRequest.of(0, 5), shop)
			.stream()
			.map(this::convertToResponse)
			.collect(Collectors.toList());
	}
} 
