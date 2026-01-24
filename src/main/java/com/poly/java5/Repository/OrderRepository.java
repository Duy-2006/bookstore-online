package com.poly.java5.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.java5.Entity.Order;
public interface OrderRepository extends JpaRepository<Order, Integer>{
	 @Query(
		        "SELECT o FROM Order o " +
		        "LEFT JOIN FETCH o.orderDetails d " +
		        "LEFT JOIN FETCH d.book " +
		        "WHERE o.orderCode = :code"
		    )
	 Optional<Order> findByOrderCodeFull(@Param("code") String code);
}
