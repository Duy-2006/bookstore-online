package com.poly.java5.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString; // Đã thêm import

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Orders")
public class Order {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
    
    @Column(name = "order_code", nullable = false, unique = true, length = 20)
    private String orderCode;
    
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;
    
    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;
    
    @Column(name = "customer_address", nullable = false, columnDefinition = "NVARCHAR(500)")
    private String customerAddress;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Builder.Default
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "PENDING";

    @Builder.Default
    @Column(name = "status", length = 20)
    private String status = "PENDING";


    @Builder.Default
    @Column(name = "payment_method", length = 20)
    private String paymentMethod = "COD";
    
    @Column(name = "order_date")
    private LocalDateTime orderDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderDetail> orderDetails;
    
    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (orderCode == null) {
            // Tự động tạo order code
            orderCode = "ORD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
    }
    
    // Business methods
    public boolean isCancellable() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }
    
    public void cancelOrder() {
        if (isCancellable()) {
            this.status = "CANCELLED";
            // Hoàn lại số lượng tồn kho
            if (orderDetails != null) {
                orderDetails.forEach(detail -> {
                    Book book = detail.getBook();
                    book.increaseStock(detail.getQuantity());
                });
            }
        }
    }
    
    // Tính tổng tiền từ order details
    public BigDecimal calculateTotal() {
        if (orderDetails != null) {
            return orderDetails.stream()
                    .map(OrderDetail::calculateSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }



    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude // Ngắt để không in ngược lại User
    private User user; 

//    @OneToMany(mappedBy = "order")
//    @ToString.Exclude // Ngắt để không in danh sách chi tiết đơn hàng
//    private List<OrderDetail> orderDetails;
}