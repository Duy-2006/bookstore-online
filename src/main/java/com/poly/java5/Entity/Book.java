package com.poly.java5.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString; // Đã thêm import này

@Data
@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Tên sách không được để trống")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "ISBN không được để trống")
    private String isbn;
    
    
//    @ManyToOne
    @NotBlank(message = "Tác giả không được để trống")
    @Column(name = "author")
    @ToString.Exclude // Ngắt toString tại đây để tránh vòng lặp với Author
    private String author;

    @Column(name = "publisher", length = 100)
    private String publisher;

    @Min(value = 0, message = "Giá nhập phải dương")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;


    @Min(value = 0, message = "Số lượng phải dương")
    @Column(name = "stock_quantity")
    private Integer quantity;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    
    @Column(columnDefinition = "nvarchar(MAX)")
    private String description;

    private Boolean active = true; // Trạng thái: Đang bán / Ngừng bán
    
    
    private Boolean deleted = false; // Soft delete: false = chưa xóa


    @ManyToOne
    @JoinColumn(name = "category_id")
    @ToString.Exclude // Ngắt toString tại đây để tránh vòng lặp với Category
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
       
		return quantity != null && getQuantity() > 0;
    }
    
    public void decreaseStock(Integer quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
        } else {
            throw new IllegalArgumentException("Không đủ hàng trong kho");
        }
    }
    
    public void increaseStock(Integer quantity) {
        if (quantity > 0) {
            this.quantity += quantity;
        }
    }
    
    // Tính tổng giá tiền (giá * số lượng)
    public BigDecimal calculateTotalPrice(Integer quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}