package in.kr.main.io;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateRequest {
	@NotBlank(message = "Shop Name not be empty")
	private String shopName;
	@NotBlank(message = "name Required")
	private String name;
	@NotBlank(message = "email Required")
	@Email(message = "Please Enter valid email")
	private String email;
	@NotBlank(message = "Address Required")
	private String shopAddress;
}
