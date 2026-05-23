package in.kr.main.service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.enums.Role;
import in.kr.main.exceptions.EmailAlreadyExistsException;
import in.kr.main.io.ProfileResponse;
import in.kr.main.io.UserRequest;
import in.kr.main.io.UserResponse;
import in.kr.main.io.UserUpdateRequest;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordencoder;
	
	public UserResponse createUser(UserRequest request, String adminEmail) {
			if(userRepository.existsByEmail(request.getEmail())) {
				System.out.println("Email Already Exist");
				throw new EmailAlreadyExistsException("Email Already Exist");
			}
			UserEntity adminUser = userRepository.findByEmail(adminEmail).orElseThrow(()->new UsernameNotFoundException("User not exists witth this email " +adminEmail));
			ShopEntity shop =  adminUser.getShop();
			UserEntity newUser = convertToEntity(request, shop);
			newUser = userRepository.save(newUser);
			return convertToResponse(newUser);
	}
	
	private UserEntity convertToEntity(UserRequest request, ShopEntity shop) {
		UserEntity entity = UserEntity.builder()
				.userId(UUID.randomUUID().toString())
				.name(request.getName())
				.password(passwordencoder.encode(request.getPassword()))
				.email(request.getEmail())
				.shop(shop)
				.role(Role.USER)
				.build();
		return entity;
	}
	
	private UserResponse convertToResponse(UserEntity newUser) {
		UserResponse response = UserResponse.builder()
				.name(newUser.getName())
				.email(newUser.getEmail())
				.createdAt(newUser.getCreatedAt())
				.updatedAt(newUser.getUpdatedAt())
				.role(Role.USER)
				.userId(newUser.getUserId())
				.build();
		return response;
	}
	
	public Role getUserRole(String email) {
		UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User Not found for this email" + email));
		return existingUser.getRole();
	}
	
	public List<UserResponse> readUsers(String email){
		UserEntity adminUser = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not exists witth this email " +email));
		ShopEntity shop =  adminUser.getShop();
		return userRepository.findByShop(shop)
			.stream()
			.filter(user->user.getRole().name()!= "ADMIN")
			.map(user->convertToResponse(user))
			.collect(Collectors.toList());
	}
	
	public void deleteUser(String userId, String adminEmail) {
		UserEntity adminUser = userRepository.findByEmail(adminEmail).orElseThrow(()->new UsernameNotFoundException("Admin not found with this email " + adminEmail)); 
		ShopEntity shop = adminUser.getShop();
		UserEntity existingUser = userRepository.findByUserIdAndShop(userId, shop).orElseThrow(()->new UsernameNotFoundException("User not found with this shop and  id " + userId));
		userRepository.delete(existingUser);
	}

	
	public ProfileResponse updateUser(UserUpdateRequest request, String email) {
		UserEntity employee = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Admin not found with this email " + email)); 
		ShopEntity shop = employee.getShop();
		employee.setName(request.getName());
		userRepository.save(employee);
		return convertToResponse(employee, shop);
	}

	private ProfileResponse convertToResponse(UserEntity employee, ShopEntity shop) {
		ProfileResponse response = ProfileResponse.builder()
				.email(employee.getEmail())
				.GSTIN(shop.getGSTNumber())
				.shopName(shop.getShopName())
				.name(employee.getName())
				.role(employee.getRole().name())
				.shopAddress(shop.getShopAddress())
				.userId(employee.getUserId())
				.shopId(shop.getShopId())
				.build();
		return response;
	}
}
