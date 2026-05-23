package in.kr.main.io;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
	private String itemId;
	private String name;
	private Double price;
	private String description;
	private String categoryId;
	private String categoryName;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	private Integer availableStock;
	private String imgUrl;
}
