package in.kr.main.entity;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shops_tbl")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String shopId;
	private String shopName;
	@Column(unique = true, nullable = true)
	private String GSTNumber;
	@Column(nullable = false)
	private String shopAddress;
	private String city;
	private String state;
	private String pincode;
	@CreationTimestamp
	@Column(updatable = false)
	private String createdAt;
	@UpdateTimestamp
	private String updatedAt;
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
	private List<UserEntity> users = new ArrayList<UserEntity>();
	
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
	private List<CategoryEntity> categories = new ArrayList<CategoryEntity>();
	
	@OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
	private List<OrderEntity> orders = new ArrayList<OrderEntity>();
}

