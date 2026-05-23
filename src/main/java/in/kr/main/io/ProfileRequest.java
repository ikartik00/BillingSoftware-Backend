package in.kr.main.io;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {
	@NotBlank(message = "Name Should Not be Empty")
	private String ownerName;
	@Email(message = "Please Enter valid Email")
	@NotNull(message = "Email Shoould not be empty")
	private String email;
	@Size(min = 6, message = "password must be atleast 6 characters")
	private String password;
	private String shopName;
	private String GSTNumber;
	@NotNull(message = "Address is required")
	private String shopAddress;
	@NotNull(message = "City is required")
	private String city;
	@NotNull(message = "State is required")
	private String state;
	@NotNull(message = "pincode is required")
	private String pincode;
}
