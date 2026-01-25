package com.poly.java5.Entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "OrderDetails")
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