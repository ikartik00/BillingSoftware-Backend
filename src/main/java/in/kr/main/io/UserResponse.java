package in.kr.main.io;

import java.sql.Timestamp;

import in.kr.main.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private String userId;
	private String name;
	private String email;
	private Role role;
	private Timestamp createdAt;
	private Timestamp updatedAt;
}
