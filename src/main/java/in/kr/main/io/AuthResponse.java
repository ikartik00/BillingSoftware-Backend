package in.kr.main.io;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
	private String email;
	private String token;
	private String role;
}
