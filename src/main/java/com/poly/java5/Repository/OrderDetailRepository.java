// File: OrderDetailRepository.java
package com.poly.java5.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.java5.Entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}