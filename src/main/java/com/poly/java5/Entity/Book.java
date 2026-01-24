package com.poly.java5.Entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.persistence.*;

@Entity
@Table(name = "Books")
@Data                       // Tạo getter/setter, toString, equals, hashCode
@NoArgsConstructor          // Constructor không tham số
@AllArgsConstructor         // Constructor có tất cả tham số
@Builder           
public class Book {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
	    
	    @Column(name = "title", nullable = false, length = 200)
	    private String title;
	    
	    @Column(name = "author", nullable = false, length = 100)
	    private String author;
	    
	    @Column(name = "publisher", length = 100)
	    private String publisher;
	    
	    @Column(name = "publish_year")
	    private Integer publishYear;
	    
	    @Column(name = "isbn", length = 20)
	    private String isbn;
	    
	    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
	    private String description;
	    
	    @Column(name = "price", nullable = false, precision = 10, scale = 2)
	    private BigDecimal price;
	    
	    @Column(name = "stock_quantity")
	    private Integer stockQuantity = 0;
	    
	    @Column(name = "image_url", length = 500)
	    private String imageUrl;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_Books_Category"))
	    @ToString.Exclude  // Không include trong toString để tránh vòng lặp vô hạn
	    @EqualsAndHashCode.Exclude  // Không include trong equals/hashCode
	    private Category category;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "seller_id", foreignKey = @ForeignKey(name = "FK_Books_Seller"))
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private User seller;
	    
	    @Column(name = "created_date", updatable = false)
	    private LocalDateTime createdDate;
	    
	    // OneToMany relationships (nếu cần)
	    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Review> reviews;
	    
	    @OneToMany(mappedBy = "book")
	    private List<CartDetail> cartDetails;

	    
	    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<OrderDetail> orderDetails;
	    
	    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private List<Wishlist> wishlists;
	    
	    // PrePersist callback - tự động set createdDate trước khi lưu
	    @PrePersist
	    protected void onCreate() {
	        createdDate = LocalDateTime.now();
	    }
	    
	    // Business logic methods
	    public boolean isAvailable() {
	        return stockQuantity != null && stockQuantity > 0;
	    }
	    
	    public void decreaseStock(Integer quantity) {
	        if (this.stockQuantity >= quantity) {
	            this.stockQuantity -= quantity;
	        } else {
	            throw new IllegalArgumentException("Không đủ hàng trong kho");
	        }
	    }
	    
	    public void increaseStock(Integer quantity) {
	        if (quantity > 0) {
	            this.stockQuantity += quantity;
	        }
	    }
	    
	    // Tính tổng giá tiền (giá * số lượng)
	    public BigDecimal calculateTotalPrice(Integer quantity) {
	        return price.multiply(BigDecimal.valueOf(quantity));
	    }
}
