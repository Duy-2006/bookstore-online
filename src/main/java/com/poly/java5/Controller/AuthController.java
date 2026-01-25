package com.poly.java5.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.java5.Entity.User;
import com.poly.java5.Repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    UserRepository userRepo;
    
    @Autowired
    HttpSession session;

    // Trang đăng nhập
    @GetMapping("/dangnhap")
    public String showLoginForm() {
        return "login"; // Trỏ đến file templates/login.html
    }

    // Xử lý đăng nhập
    @PostMapping("/dangnhap")
    public String dangnhap(@RequestParam("username") String username, 
                        @RequestParam("password") String password,
                        Model model) {
    	User user = userRepo.findByUsername(username);

        
        // Check pass thô (Trong thực tế phải dùng BCrypt)
        if (user != null && user.getPassword().equals(password)) {
            if(!user.getActive()) {
                model.addAttribute("error", "Tài khoản đã bị khóa!");
                return "login";
            }
            
            // Lưu user vào session
            session.setAttribute("currentUser", user);
            
            // Điều hướng dựa trên Role
            if (user.getRole().name().equals("ADMIN") || user.getRole().name().equals("STAFF")) {
                return "redirect:/admin";
            } else {
                return "redirect:/"; // User thường về trang chủ (chưa làm thì về login)
            }
        }
        
        model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        return "login";
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout() {
        session.removeAttribute("currentUser");
        return "redirect:/login";
    }
}