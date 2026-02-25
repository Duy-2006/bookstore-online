package com.poly.java5.Controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.java5.Entity.Order;
import com.poly.java5.Service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
	 private final OrderService orderService;

	 @GetMapping("/orders")
	 public String listOrders(Model model) {
	     List<Order> orders = orderService.findAll();
	     model.addAttribute("orders", orders);
	     return "orders"; 
	 }
	 
	 @GetMapping("/order-success")
	 public String success(@RequestParam("code") String code, Model model) {
	     Order order = orderService.findByCode(code);

	     model.addAttribute("order", order);
	     model.addAttribute("orderDetails", order.getOrderDetails());

	     return "order-success";
	 }
	 
	 
}
