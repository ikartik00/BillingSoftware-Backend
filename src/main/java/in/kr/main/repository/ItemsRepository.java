package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.kr.main.entity.CategoryEntity;
import in.kr.main.entity.ItemsEntity;

@Repository
public interface ItemsRepository extends JpaRepository<ItemsEntity, Long> {
	Optional<ItemsEntity> findByItemId(String itemId);

	long countByCategoryId(Long categoryId);

	boolean existsByCategoryName(String name);

	boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);
	
	@Query("select i from ItemsEntity i where i.category.shop.id = :shopId ")
	List<ItemsEntity> findAllItemsByShop(Long shopId);

	long countByCategory_CategoryId(String id);

	long countByCategory(CategoryEntity entity);

	boolean existsByItemId(String itemId);

	boolean existsByNameIgnoreCase(String name);
}
