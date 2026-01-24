package com.poly.java5.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poly.java5.Entity.Order;
import com.poly.java5.Entity.OrderDetail;
import com.poly.java5.Entity.User;
import com.poly.java5.Repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	private final OrderRepository orderRepository;

	@Transactional
	public Order createOrder(User user, String customerName, String customerPhone, String customerAddress,
			String paymentMethod, List<OrderDetail> details) {
		Order order = Order.builder().user(user).customerName(customerName).customerPhone(customerPhone)
				.customerAddress(customerAddress).paymentMethod(paymentMethod).status("PENDING")
				.paymentStatus("PENDING").build();

		details.forEach(d -> d.setOrder(order));
		order.setOrderDetails(details);

		order.setTotalAmount(order.calculateTotal());

		Order savedOrder = orderRepository.save(order); // 👈 LƯU XONG MỚI TRẢ

		return savedOrder;
	}

	public Order findByCode(String code) {
	    return orderRepository.findByOrderCodeFull(code)
	        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));
	}

}
