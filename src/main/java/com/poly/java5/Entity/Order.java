package com.poly.java5.Entity;
import lombok.*;
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

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
	    
	    @Column(name = "order_code", nullable = false, unique = true, length = 20)
	    private String orderCode;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private User user;
	    
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
}
