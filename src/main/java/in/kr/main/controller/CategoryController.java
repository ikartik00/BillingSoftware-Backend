package in.kr.main.controller;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import in.kr.main.entity.CategoryEntity;
import in.kr.main.io.CategoryRequest;
import in.kr.main.io.CategoryResponse;
import in.kr.main.service.CategoryService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.thirdparty.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;
	@PostMapping("admin/categories/add_category")
	public ResponseEntity<CategoryResponse> addCategory(@RequestPart("category") String categoryString, @RequestPart("file") MultipartFile file) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		ObjectMapper objectMapper = new ObjectMapper();
		CategoryRequest request = null;
		try {
			request = objectMapper.readValue(categoryString, CategoryRequest.class);
			return new ResponseEntity<>(categoryService.addCategory(request,email, file), HttpStatus.CREATED);
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception ocuur while parsing the json" + e.getMessage());
		}
	}
	
	@GetMapping("/categories/all")
	public List<CategoryResponse> getAllCategories(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return categoryService.getAllCategories(email);
	}
	
	@DeleteMapping("/admin/categories/delete/{id}")
	public ResponseEntity<String> deleteCategory(@PathVariable String id) {
		try {
			categoryService.deleteCategory(id);
			return new ResponseEntity<String>("Category Deleted", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<String>("Entity not Deleted " + e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/admin/update/{id}")
	public CategoryResponse updateProduct(@PathVariable String id,  @RequestPart("category") String categoryString, @RequestPart("file") MultipartFile file) {
		ObjectMapper objectMapper = new ObjectMapper();
		CategoryRequest request = null;
		try {
			request = objectMapper.readValue(categoryString, CategoryRequest.class);
			return categoryService.updateProduct(id, request, file);
		}catch(Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exception ocuur while parsing the json " + e.getMessage());
		}
	}
}
