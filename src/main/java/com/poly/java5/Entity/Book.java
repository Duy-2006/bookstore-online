package com.poly.java5.Entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Entity
@Table(name = "Books")
public class Book implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1. VALIDATION CƠ BẢN
    @NotBlank(message = "Tên sách không được để trống")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Mã ISBN không được để trống")
    @Column(length = 20)
    private String isbn;

    // 2. GIÁ TIỀN & SỐ LƯỢNG (QUAN TRỌNG)
    @NotNull(message = "Giá bán không được để trống")
    @Min(value = 1000, message = "Giá bán phải từ 1.000 VNĐ trở lên")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Dùng BigDecimal để tính tiền chính xác

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    @Column(name = "stock_quantity", nullable = false)
    private Integer quantity;

    // 3. THÔNG TIN KHÁC
    @Column(length = 100)
    private String publisher;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "nvarchar(MAX)")
    private String description;

    private Boolean active = true;  // Đang kinh doanh
    private Boolean deleted = false; // Xóa mềm (Soft delete)

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    // 4. QUAN HỆ (RELATIONSHIPS) - CÓ VALIDATION
    @NotNull(message = "Vui lòng chọn tác giả")
    @ManyToOne
    @JoinColumn(name = "author_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Author author;

    @NotNull(message = "Vui lòng chọn thể loại")
    @ManyToOne
    @JoinColumn(name = "category_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Category category;

    // Quan hệ với người bán (nếu có hệ thống Multi-vendor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User seller;

    // 5. CÁC LIST QUAN HỆ (ONE-TO-MANY)
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Review> reviews; // Đánh giá sách

    @OneToMany(mappedBy = "book")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CartDetail> cartDetails; // Chi tiết giỏ hàng

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderDetail> orderDetails; // Chi tiết đơn hàng

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Wishlist> wishlists; // Danh sách yêu thích

    // 6. AUTO SET DATE
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    // 7. NGHIỆP VỤ (BUSINESS LOGIC)
    
    // Kiểm tra còn hàng hay không
    public boolean isAvailable() {
        return quantity != null && quantity > 0 && Boolean.TRUE.equals(active);
    }

    // Giảm tồn kho (Khi có đơn hàng)
    public void decreaseStock(Integer amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Kho không đủ hàng!");
        }
        this.quantity -= amount;
    }

    // Tăng tồn kho (Khi nhập hàng hoặc khách hủy đơn)
    public void increaseStock(Integer amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    // Tính tổng tiền (Giá * Số lượng)
    public BigDecimal calculateTotalPrice(Integer quantity) {
        if (price == null || quantity == null) return BigDecimal.ZERO;
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}