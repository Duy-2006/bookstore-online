// File: OrderRepository.java
package com.poly.java5.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poly.java5.Entity.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy danh sách đơn hàng giảm dần theo ngày tạo (Mới nhất lên đầu)
    List<Order> findAllByOrderByCreateDateDesc();
}