package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Entity.Payment;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @GetMapping("/init")
    public String initPaymentForm(Model model) {
        model.addAttribute("payment", new Payment());
        return "payment/init";
    }

    @PostMapping("/process")
    public String processPayment(@Valid @ModelAttribute Payment payment, BindingResult result) {
        if (result.hasErrors()) {
            return "payment/init";
        }
        // Chưa xử lý logic: Tích hợp cổng thanh toán (e.g., redirect to gateway)
        return "redirect:/payments/success";
    }

    @GetMapping("/success")
    public String paymentSuccess() {
        return "payment/success";
    }
}