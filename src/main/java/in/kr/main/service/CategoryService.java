package in.kr.main.service;
import in.kr.main.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.kr.main.entity.CategoryEntity;
import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.exceptions.CategoryAlreadyExistsException;
import in.kr.main.io.CategoryRequest;
import in.kr.main.io.CategoryResponse;
import in.kr.main.repository.CategoryRepository;
import in.kr.main.repository.ItemsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final FileUploadService fileUploadService;
	private final ItemsRepository itemsRepository;

	
	public CategoryResponse addCategory(CategoryRequest request,String email, MultipartFile file) {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with this email " + email));
		ShopEntity shop = userEntity.getShop();
		if(categoryRepository.existsByNameAndShop(request.getName(), shop)) {
			throw new CategoryAlreadyExistsException("Category Already Exists with this name and shop");
		}
		CategoryEntity entity = convertToEntity(request, shop);
		entity.setImageUrl(fileUploadService.uploadFile(file));
		categoryRepository.save(entity);
		return convertToResponse(entity);
	}

	private CategoryResponse convertToResponse(CategoryEntity entity) {
//		long Countitems = itemsRepository.countByCategory_CategoryId(entity.getCategoryId());
		long Countitems = itemsRepository.countByCategory(entity);
		CategoryResponse response = CategoryResponse.builder()
				.bgColor(entity.getBgColor())
				.name(entity.getName())
				.categoryId(entity.getCategoryId())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.description(entity.getDescription())
				.imageUrl(entity.getImageUrl())
				.items(Countitems)
				.shopId(entity.getShop().getShopId())
				.build();
		return response;
	}

	private CategoryEntity convertToEntity(CategoryRequest request, ShopEntity shop) {
		CategoryEntity entity = CategoryEntity.builder()
				.categoryId(UUID.randomUUID().toString())
				.bgColor(request.getBgColor())
				.description(request.getDescription())
				.name(request.getName())
				.shop(shop)	
				.build();
		return entity;
	}
	public List<CategoryResponse> getAllCategories(String email){
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with this email " + email));
		ShopEntity shop = userEntity.getShop();
		List<CategoryEntity> allCategories = categoryRepository.findByShop(shop);
		return convertToListResponse(allCategories);
	}

	private List<CategoryResponse> convertToListResponse(List<CategoryEntity> allEntities) {
		List<CategoryResponse> allResponse = new ArrayList<CategoryResponse>();
		for(CategoryEntity entity : allEntities){
			long Countitems = itemsRepository.countByCategory(entity);
			CategoryResponse response = CategoryResponse.builder()
					.bgColor(entity.getBgColor())
					.name(entity.getName())
					.items(Countitems)
					.categoryId(entity.getCategoryId())
					.createdAt(entity.getCreatedAt())
					.updatedAt(entity.getUpdatedAt())
					.description(entity.getDescription())
					.imageUrl(entity.getImageUrl())
					.shopId(entity.getShop().getShopId())
					.build();
			allResponse.add(response);
		}
		return allResponse;
	}

	public void deleteCategory(String id) {
		CategoryEntity entity =  categoryRepository.findByCategoryId(id).orElseThrow(()->new RuntimeException("Category Not Found " + id));
		if(entity != null) {
			fileUploadService.deleteFile(entity.getImageUrl());
			categoryRepository.delete(entity);
		}else {
			throw new EntityNotFoundException("Category Not Found " + id);
		}
	}

	public CategoryResponse updateProduct(String id, CategoryRequest request, MultipartFile file) {
		CategoryEntity entity =  categoryRepository.findByCategoryId(id).orElseThrow(()->new EntityNotFoundException("Category Not Found " + id));
		if(entity != null) {
			entity.setBgColor(request.getBgColor());
			entity.setDescription(request.getDescription());
			entity.setName(request.getName());
			fileUploadService.deleteFile(entity.getImageUrl());
			entity.setImageUrl(fileUploadService.uploadFile(file));
			categoryRepository.save(entity);
			CategoryResponse response = convertToResponse(entity);
			return response;
		}else {
			throw new EntityNotFoundException("Category Not Found " + id);
		}
	}
}
