package in.kr.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.kr.main.entity.ShopEntity;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long> {
	boolean existsByGSTNumber(String gstNumber);

}
