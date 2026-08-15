package in.kr.main.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import in.kr.main.entity.CategoryEntity;
import in.kr.main.entity.ItemsEntity;
import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.StockEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.exceptions.CategoryNotFoundException;
import in.kr.main.exceptions.ItemAlreadyExistsException;
import in.kr.main.exceptions.ItemNotExistException;
import in.kr.main.io.ItemRequest;
import in.kr.main.io.ItemResponse;
import in.kr.main.repository.CategoryRepository;
import in.kr.main.repository.ItemsRepository;
import in.kr.main.repository.StockRepository;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemsService {
	private final ItemsRepository itemsRepository;
	private final FileUploadService fileUploadService;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final StockRepository stockRepository;

	public ItemResponse addItem(ItemRequest request, MultipartFile file, String email) {
		UserEntity adminUser = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not exists witth this email " +email));
		CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId()).orElseThrow(()-> new CategoryNotFoundException("Category Not Found with this id " + request.getCategoryId()));
		ShopEntity shop =  adminUser.getShop();
		if(itemsRepository.existsByNameIgnoreCase(request.getName())) {
			throw new ItemAlreadyExistsException("item is already exists with this name " + request.getName());
		}
		if(!existingCategory.getShop().getShopId().equals(shop.getShopId())) {
			throw new RuntimeException("Shop and category mismatch");
		}
		
		ItemsEntity itemEntity = convertToEntity(request, existingCategory);
		StockEntity stock = new StockEntity();
		stock.setAvailableStock(0);
		stock.setItem(itemEntity);
		itemEntity.setStock(stock);
		itemEntity.setImageUrl(fileUploadService.uploadFile(file));
		itemsRepository.save(itemEntity);
		return convertToResponse(itemEntity);
	}

	private ItemResponse convertToResponse(ItemsEntity itemEntity) {
//		Integer availableStock = stockRepository.countByItem(itemEntity);
		ItemResponse response = ItemResponse.builder()
				.name(itemEntity.getName())
				.price(itemEntity.getPrice())
				.description(itemEntity.getDescription())
				.categoryId(itemEntity.getCategory().getCategoryId())
				.itemId(itemEntity.getItemId())
				.createdAt(itemEntity.getCreatedAt())
				.updatedAt(itemEntity.getUpdatedAt())
				.availableStock(itemEntity.getStock() == null ? 0 : itemEntity.getStock().getAvailableStock())
				.imgUrl(itemEntity.getImageUrl())
				.categoryName(itemEntity.getCategory().getName())
				.build();
		return response;
	}

	private ItemsEntity convertToEntity(ItemRequest request, CategoryEntity existingCategory) {
		ItemsEntity entity = ItemsEntity.builder()
				.name(request.getName())
				.price(request.getPrice())
				.description(request.getDescription())
				.itemId(UUID.randomUUID().toString())
				.category(existingCategory)
				.build();
		return entity;
	}
	
	public List<ItemResponse> getAllItems(String email){
		UserEntity adminUser = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not exists witth this email " +email));
		ShopEntity shop =  adminUser.getShop();
		return itemsRepository.findAllItemsByShop(shop.getId())
			.stream()
			.map(item-> convertToResponse(item))
			.collect(Collectors.toList());
	}
	
	public void deleteItem(String itemId, String email) {
		ItemsEntity itemEntity = itemsRepository.findByItemId(itemId).orElseThrow(()->new ItemNotExistException("Item Not exist with this id " + itemId));
		UserEntity adminUser = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not exists witth this email " +email));
		ShopEntity shop =  adminUser.getShop();
		if(itemEntity.getCategory().getShop().getId() != shop.getId()) {
			throw new RuntimeException("Shop and item doesn't match");
		}
		
		boolean isFileDeleted = fileUploadService.deleteFile(itemEntity.getImageUrl());
		if(isFileDeleted) {
			itemsRepository.delete(itemEntity);
		}else {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to delete the image");
		}
	}
}
