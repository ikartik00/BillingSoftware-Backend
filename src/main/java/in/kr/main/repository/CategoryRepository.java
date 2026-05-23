package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.kr.main.entity.CategoryEntity;
import in.kr.main.entity.ShopEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

	Optional<CategoryEntity> findByCategoryId(String id);

	boolean existsByNameAndShop(String name, ShopEntity shop);

	List<CategoryEntity> findByShop(ShopEntity shop);
	
}
