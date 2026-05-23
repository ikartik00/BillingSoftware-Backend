package in.kr.main.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import in.kr.main.io.ProfileResponse;
import in.kr.main.io.UserRequest;
import in.kr.main.io.UserResponse;
import in.kr.main.io.UserUpdateRequest;
import in.kr.main.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	
	@PostMapping("/admin/register")
	public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String adminEmail = auth.getName();
			return new ResponseEntity<UserResponse>(userService.createUser(request, adminEmail), HttpStatus.CREATED);
	}
	
	@GetMapping("/admin/users")
	public List<UserResponse> getAllUsers(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return userService.readUsers(email);
	}
	
	@DeleteMapping("/admin/users/delete/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable String userId){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	String adminEmail = auth.getName();
			userService.deleteUser(userId, adminEmail);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/user/update")
	public ResponseEntity<ProfileResponse> updateUser(@RequestBody UserUpdateRequest request){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return new ResponseEntity<ProfileResponse>(userService.updateUser(request, email), HttpStatus.OK);
	}
	
}
