package in.kr.main.io;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
	@NotBlank(message = "Item Id Cannot be null and empty")
	private String itemId;
//	@NotBlank(message = "quantity can't be null")
	@Positive(message = "Quantity must be greater than 0")
	private Integer quantity;
}
