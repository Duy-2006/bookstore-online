package com.poly.java5.Controller;
import com.poly.java5.Service.BookService;
import com.poly.java5.Service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
	private final CartService cartService;
    private final BookService bookService;
    
    private Integer getUserId(HttpSession session) {
        return (Integer) session.getAttribute("USER_ID");
    }
    
    // ============ VIEW PAGES ============
    
    // Trang chủ sản phẩm
    @GetMapping("/products")
    public String viewProducts(Model model, HttpSession session) {
        log.info("Hiển thị trang sản phẩm");
        
        Integer userId = getUserId(session); // khởi tạo session
        if (userId == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("books", bookService.getAllBooks());
        int cartCount = cartService.getCartItemCount(userId);
        model.addAttribute("cartCount", cartCount);

        
        return "cart/products";
    }
    
    // Trang giỏ hàng
    @GetMapping
    public String viewCart(Model model, HttpSession session) {
        log.info("Hiển thị trang giỏ hàng");

        Integer userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Map<String, Object> cartSummary = cartService.getCartSummary(userId);

        model.addAttribute("cart", cartSummary.get("cart"));
        model.addAttribute("cartItems", cartSummary.get("cartItems"));
        model.addAttribute("totalItems", cartSummary.get("totalItems"));
        model.addAttribute("totalAmount", cartSummary.get("totalAmount"));
        model.addAttribute("finalAmount", cartSummary.get("finalAmount"));

        return "cart";
    }

    
    // ============ API ENDPOINTS ============
    
    // API: Thêm vào giỏ hàng
    @PostMapping("/api/add")
    @ResponseBody
    public Map<String, Object> addToCartApi(
            @RequestParam Integer bookId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {
        
        log.info("API: Thêm vào giỏ hàng - bookId: {}, quantity: {}", bookId, quantity);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = getUserId(session);
            if (userId == null) {
                throw new RuntimeException("Chưa đăng nhập");
            }
            Map<String, Object> result = cartService.addToCart(userId, bookId, quantity);
            
            response.put("success", true);
            response.put("message", "Đã thêm vào giỏ hàng");
            response.put("data", result);
            response.put("cartCount", cartService.getCartItemCount(userId));
            
        } catch (Exception e) {
            log.error("Lỗi khi thêm vào giỏ hàng: ", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // API: Cập nhật số lượng
    @PostMapping("/api/update")
    @ResponseBody
    public Map<String, Object> updateCartApi(
            @RequestParam Integer cartDetailId,
            @RequestParam Integer quantity,
            HttpSession session) {
        
        log.info("API: Cập nhật giỏ hàng - cartDetailId: {}, quantity: {}", cartDetailId, quantity);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = getUserId(session);
            if (userId == null) {
                throw new RuntimeException("Chưa đăng nhập");
            }
            Map<String, Object> result = cartService.updateCartItem(userId, cartDetailId, quantity);
            
            response.put("success", true);
            response.put("message", "Đã cập nhật giỏ hàng");
            response.put("data", result);
            
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật giỏ hàng: ", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // API: Xóa sản phẩm
    @PostMapping("/api/remove")
    @ResponseBody
    public Map<String, Object> removeFromCartApi(
            @RequestParam Integer cartDetailId,
            HttpSession session) {
        
        log.info("API: Xóa khỏi giỏ hàng - cartDetailId: {}", cartDetailId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = getUserId(session);
            if (userId == null) {
                throw new RuntimeException("Chưa đăng nhập");
            }
            Map<String, Object> result = cartService.removeFromCart(userId, cartDetailId);
            
            response.put("success", true);
            response.put("message", "Đã xóa sản phẩm");
            response.put("data", result);
            
        } catch (Exception e) {
            log.error("Lỗi khi xóa khỏi giỏ hàng: ", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // API: Xóa toàn bộ giỏ hàng
    @PostMapping("/api/clear")
    @ResponseBody
    public Map<String, Object> clearCartApi(HttpSession session) {
        log.info("API: Xóa toàn bộ giỏ hàng");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = getUserId(session);
            if (userId == null) {
                throw new RuntimeException("Chưa đăng nhập");
            }
            cartService.clearCart(userId);
            
            response.put("success", true);
            response.put("message", "Đã xóa toàn bộ giỏ hàng");
            
        } catch (Exception e) {
            log.error("Lỗi khi xóa giỏ hàng: ", e);
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // API: Lấy thông tin giỏ hàng
    @GetMapping("/api/summary")
    @ResponseBody
    public Map<String, Object> getCartSummaryApi(HttpSession session) {
        log.info("API: Lấy thông tin giỏ hàng");
        
        Integer userId = getUserId(session);
        if (userId == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }
        return cartService.getCartSummary(userId);
    }
    
    // API: Đếm số lượng
    @GetMapping("/api/count")
    @ResponseBody
    public Map<String, Object> getCartCountApi(HttpSession session) {
        log.info("API: Đếm số lượng sản phẩm trong giỏ");
        
        Integer userId = getUserId(session);
        int count = cartService.getCartItemCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("userId", userId);
        
        return response;
    }
}
