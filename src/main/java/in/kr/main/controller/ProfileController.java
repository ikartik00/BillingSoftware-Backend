package in.kr.main.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.io.AdminUpdateRequest;
import in.kr.main.io.ProfileRequest;
import in.kr.main.io.ProfileResponse;
import in.kr.main.io.UserUpdateRequest;
import in.kr.main.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfileController {
		private final ProfileService profileService;
		@PostMapping("/register")
		public ResponseEntity<ProfileResponse> register(@Valid @RequestBody ProfileRequest request) {
			ProfileResponse response = profileService.createProfile(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
		
		@GetMapping("/profile")
		public ResponseEntity<ProfileResponse> getUserProfile(){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String email = auth.getName();
			ProfileResponse response = profileService.getUserProfile(email);
			return new ResponseEntity<ProfileResponse>(response, HttpStatus.OK);
		}
		
		@PutMapping("/admin/update")
		public ResponseEntity<ProfileResponse> editProfile(@RequestBody AdminUpdateRequest request ){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String email = auth.getName();
			ProfileResponse response = profileService.editProfile(request, email);
			return new ResponseEntity<ProfileResponse>(response, HttpStatus.OK);
		}
}
