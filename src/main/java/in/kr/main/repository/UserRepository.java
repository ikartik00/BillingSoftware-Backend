package in.kr.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	Optional<UserEntity> findByEmail(String email);

	 Optional<UserEntity> findByUserId(String id);

	 boolean existsByEmail(String email);

	 List<UserEntity> findByShop(ShopEntity shop);

	 Optional<UserEntity> findByUserIdAndShop(String userId, ShopEntity shop);
}
