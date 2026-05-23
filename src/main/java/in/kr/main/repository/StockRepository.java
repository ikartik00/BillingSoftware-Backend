package in.kr.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kr.main.entity.ItemsEntity;
import in.kr.main.entity.StockEntity;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

	StockEntity findByItem(ItemsEntity existingItem);

}
