package in.kr.main.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import in.kr.main.io.ItemRequest;
import in.kr.main.io.ItemResponse;
import in.kr.main.service.ItemsService;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
public class ItemsController {
	private final ItemsService itemsService;
	@PostMapping("/admin/addItem")
	public ResponseEntity<ItemResponse> addItem(@RequestPart("item") String itemString,
			@RequestPart("file") MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();
		ItemRequest request = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		try {
			request = objectMapper.readValue(itemString, ItemRequest.class);
			return new ResponseEntity<ItemResponse>(itemsService.addItem(request, file, email), HttpStatus.CREATED);
		} catch (HttpMessageNotReadableException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Errror occured while processing the json");
		}
	}

	@GetMapping("/items")
	public List<ItemResponse> getAllItems() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return itemsService.getAllItems(email);
	}

	@DeleteMapping("/admin/items/delete/{itemId}")
	public ResponseEntity<String> deleteItem(@PathVariable String itemId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
			itemsService.deleteItem(itemId, email);
			return new ResponseEntity<String>("Deleted", HttpStatus.NO_CONTENT);
		
	}
}
