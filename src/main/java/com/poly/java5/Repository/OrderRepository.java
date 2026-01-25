// File: OrderRepository.java
package com.poly.java5.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.java5.Entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Lấy danh sách đơn hàng giảm dần theo ngày tạo (Mới nhất lên đầu)
	List<Order> findAllByOrderByOrderDateDesc();
    
    @Query(
	        "SELECT o FROM Order o " +
	        "LEFT JOIN FETCH o.orderDetails d " +
	        "LEFT JOIN FETCH d.book " +
	        "WHERE o.orderCode = :code"
	    )
 Optional<Order> findByOrderCodeFull(@Param("code") String code);
}