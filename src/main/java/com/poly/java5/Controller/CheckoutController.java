package com.poly.java5.Controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.poly.java5.Bean.CheckoutBean;
import com.poly.java5.Entity.Order;
import com.poly.java5.Service.CartService;
import com.poly.java5.Service.CheckoutService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class CheckoutController {
	private final CartService cartService;
    private final CheckoutService checkoutService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
    	   Integer userId = (Integer) session.getAttribute("USER_ID");
    	    if (userId == null) return "redirect:/login";

    	    Map<String, Object> cart = cartService.getCartSummary(userId);

    	    BigDecimal totalAmount = (BigDecimal) cart.get("totalAmount");
    	    BigDecimal shippingFee = BigDecimal.valueOf(15000);
    	    BigDecimal discount = BigDecimal.ZERO;

    	    if (totalAmount == null) {
    	        totalAmount = BigDecimal.ZERO;
    	    }

    	    BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discount);

    	    model.addAttribute("cartDetails", cart.get("cartItems"));
    	    model.addAttribute("totalAmount", totalAmount);
    	    model.addAttribute("shippingFee", shippingFee);
    	    model.addAttribute("discount", discount);
    	    model.addAttribute("finalAmount", finalAmount);
    	    model.addAttribute("checkoutBean", new CheckoutBean());
    	  

    	    return "checkout";
    	}

    @PostMapping("/checkout")
    public String checkout(
            @Valid @ModelAttribute("checkoutBean") CheckoutBean bean,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        Integer userId = (Integer) session.getAttribute("USER_ID");
        if (userId == null) return "redirect:/login";

        if (result.hasErrors()) {

            // load lại dữ liệu giỏ hàng
            Map<String, Object> cart = cartService.getCartSummary(userId);

            BigDecimal totalAmount = (BigDecimal) cart.getOrDefault("totalAmount", BigDecimal.ZERO);
            BigDecimal shippingFee = BigDecimal.valueOf(15000);
            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal finalAmount = totalAmount.add(shippingFee).subtract(discount);

            model.addAttribute("cartDetails", cart.get("cartItems"));
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("discount", discount);
            model.addAttribute("finalAmount", finalAmount);

            return "checkout"; // quay lại trang + hiện lỗi
        }

        Order order = checkoutService.checkout(
                userId,
                bean.getCustomerName(),
                bean.getCustomerPhone(),
                bean.getCustomerAddress(),
                bean.getPaymentMethod()
        );

        return "redirect:/order-success?code=" + order.getOrderCode();
    }
}
