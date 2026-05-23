package in.kr.main.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.enums.Role;
import in.kr.main.exceptions.EmailAlreadyExistsException;
import in.kr.main.exceptions.GstAlreadyExistsException;
import in.kr.main.exceptions.UserAlreadyExistsException;
import in.kr.main.io.AdminUpdateRequest;
import in.kr.main.io.ProfileRequest;
import in.kr.main.io.ProfileResponse;
import in.kr.main.repository.ShopRepository;
import in.kr.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
	private final UserRepository userRepository;
	private final ShopRepository shopRepository;
	private final PasswordEncoder passwordEncoder;

	public ProfileResponse createProfile(ProfileRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("User With this email " + request.getEmail() + " Already Exists");
		}
		if (shopRepository.existsByGSTNumber(request.getGSTNumber()) && request.getGSTNumber() != null) {
			throw new GstAlreadyExistsException("Shop With this GST " + request.getGSTNumber() + " Already Exists");
		}

		ShopEntity shopEntity = convertToShopEntity(request);
		shopEntity = shopRepository.save(shopEntity);
		UserEntity userEntity = convertToEntity(request, shopEntity);
		userEntity = userRepository.save(userEntity);
		return convertToResponse(userEntity);
	}

	private ShopEntity convertToShopEntity(ProfileRequest request) {
		ShopEntity shopEntity = ShopEntity.builder().shopId(UUID.randomUUID().toString()).city(request.getCity())
				.shopAddress(request.getShopAddress()).pincode(request.getPincode())
				.GSTNumber(request.getGSTNumber() == null || request.getGSTNumber().trim().isEmpty() ? null
						: request.getGSTNumber())
				.shopName(request.getShopName()).state(request.getState()).build();
		return shopEntity;
	}

	private ProfileResponse convertToResponse(UserEntity entity) {
		ProfileResponse response = ProfileResponse.builder().name(entity.getName()).email(entity.getEmail())
				.shopId(entity.getShop().getShopId()).shopName(entity.getShop().getShopName())
				.userId(entity.getUserId()).shopAddress(entity.getShop().getShopAddress())
				.GSTIN(entity.getShop().getGSTNumber()).role(entity.getRole().name()).build();
		return response;
	}

	private UserEntity convertToEntity(ProfileRequest request, ShopEntity shop) {
		UserEntity entity = UserEntity.builder().name(request.getOwnerName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).userId(UUID.randomUUID().toString())
				.role(Role.ADMIN).shop(shop).build();
		return entity;
	}

	public ProfileResponse getUserProfile(String email) {
		UserEntity userEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not exists with this email " + email));
		return convertToResponse(userEntity);
	}

	@Transactional
	public ProfileResponse editProfile(AdminUpdateRequest request, String email) {
		UserEntity adminUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User Not found with this email" + email));
		ShopEntity shop = adminUser.getShop();

		adminUser.setName(request.getName());
		shop.setShopName(request.getShopName());
		shop.setShopAddress(request.getShopAddress());

		if (!email.equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email Already Exists with this " + request.getEmail());
		}
		if (!email.equals(request.getEmail())) {
			adminUser.setEmail(request.getEmail());
		}
		return convertToResponse(adminUser);
	}
}
