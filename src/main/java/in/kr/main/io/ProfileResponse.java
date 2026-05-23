package in.kr.main.io;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
	private String userId;
	private String shopId;
	private String shopName;
    private String name;
    private String email;
    private String GSTIN;
    private String shopAddress;
    private String role;
}

