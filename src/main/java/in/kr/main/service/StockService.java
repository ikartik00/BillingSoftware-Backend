package in.kr.main.service;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.ItemsEntity;
import in.kr.main.entity.ShopEntity;
import in.kr.main.entity.StockEntity;
import in.kr.main.entity.UserEntity;
import in.kr.main.io.ItemResponse;
import in.kr.main.io.StockRequest;
import in.kr.main.repository.ItemsRepository;
import in.kr.main.repository.StockRepository;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {
	private final StockRepository stockRepository;
	private final UserRepository userRepository;
	private final ItemsRepository itemsRepository;
	
	public ItemResponse addStock(StockRequest request, String email){
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with this email " + email));
		ShopEntity shop = userEntity.getShop();
		ItemsEntity existingItem = itemsRepository.findByItemId(request.getItemId()).orElseThrow(()-> new RuntimeException("Item withh this id not exist" + request.getItemId()));
		if(!existingItem.getCategory().getShop().getId().equals(shop.getId())) {
			throw new RuntimeException("Items and shop are different");
		}
		StockEntity stock = stockRepository.findByItem(existingItem);
		stock.setAvailableStock(stock.getAvailableStock() + request.getQuantity());
		stockRepository.save(stock);;
		return convertToResponse(existingItem);
	}
	
	private ItemResponse convertToResponse(ItemsEntity itemEntity) {
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
}
