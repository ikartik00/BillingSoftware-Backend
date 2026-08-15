package in.kr.main.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.kr.main.enums.Role;
import in.kr.main.io.AuthRequest;
import in.kr.main.io.AuthResponse;
import in.kr.main.service.AppUserDetailsService;
import in.kr.main.service.JwtService;
import in.kr.main.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final AppUserDetailsService service;
	private final JwtService jwtService;
	private final UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) throws Exception {
		try {
			authenticate(request.getEmail(), request.getPassword());
			final UserDetails userDetails = service.loadUserByUsername(request.getEmail());
			final String jwtToken = jwtService.generateToken(userDetails.getUsername());
			Role role = userService.getUserRole(request.getEmail());
			return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse( userDetails.getUsername(), jwtToken, role.name()));
		}catch (BadCredentialsException e) {
			Map<String, Object> errors = new HashMap<String, Object>();
			errors.put("errors", true);
			errors.put("message", "Email and Password is incorrect");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		} catch (DisabledException e) {
			Map<String, Object> errors = new HashMap<String, Object>();
			errors.put("errors", true);
			errors.put("message", "Account is Disabled");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
		} catch (Exception e) {
			Map<String, Object> errors = new HashMap<String, Object>();
			errors.put("errors", true);
			errors.put("message", "Authentication failed");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
		}
	}

	private void authenticate(String email, String password) throws Exception {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
	}

	@PostMapping("/encode")
	public String encodePassword(@RequestBody Map<String, String> request) {
		return passwordEncoder.encode(request.get("password"));
	}
}
