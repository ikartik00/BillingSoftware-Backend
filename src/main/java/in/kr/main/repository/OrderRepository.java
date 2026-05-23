package in.kr.main.repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.kr.main.entity.OrderEntity;
import in.kr.main.entity.ShopEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	Optional<OrderEntity> findByOrderId(String orderId);

	List<OrderEntity> findAllByShopOrderByCreatedAtDesc(ShopEntity shop);

	Optional<OrderEntity> findByOrderIdAndShop(String orderId, ShopEntity shop);
	
	@Query("SELECT SUM(o.grandTotal) FROM OrderEntity o WHERE DATE(o.createdAt) = :date and o.shop = :shop")
	Double sumSalesByDate(@Param("date") LocalDate date, @Param("shop") ShopEntity shop);
	
	@Query("SELECT COUNT(o) FROM OrderEntity o WHERE DATE(o.createdAt) = :date and o.shop = :shop")
	Long countByOrderDate(@Param("date") LocalDate date, @Param("shop") ShopEntity shop);
	
	@Query("SELECT o FROM OrderEntity o WHERE o.shop = :shop ORDER BY o.createdAt DESC")
	List<OrderEntity> findRecentOrders(Pageable pageable, @Param("shop") ShopEntity shop);
}
