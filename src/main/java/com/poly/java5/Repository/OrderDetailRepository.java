// File: OrderDetailRepository.java
package com.poly.java5.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poly.java5.Entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	
	@Query("""
		    SELECT od.book, SUM(od.quantity)
		    FROM OrderDetail od
		    JOIN od.order o
		    WHERE o.status IN ('CONFIRMED', 'COMPLETED')
		      AND o.paymentStatus = 'PAID'
		    GROUP BY od.book
		    ORDER BY SUM(od.quantity) DESC
		""")
		List<Object[]> findBestSellerBooks();


}