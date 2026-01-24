package com.poly.java5.Entity;

import lombok.*;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "OrderDetails")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private Integer id;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "order_id", nullable = false)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private Order order;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "book_id", nullable = false)
	    @ToString.Exclude
	    @EqualsAndHashCode.Exclude
	    private Book book;
	    
	    @Column(name = "quantity", nullable = false)
	    private Integer quantity;
	    
	    @Column(name = "price", nullable = false, precision = 10, scale = 2)
	    private BigDecimal price;
	    
	    // Tính subtotal cho item này
	    public BigDecimal calculateSubtotal() {
	        return price.multiply(BigDecimal.valueOf(quantity));
	    }
}
