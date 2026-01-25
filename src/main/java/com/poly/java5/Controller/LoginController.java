package com.poly.java5.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Bean.LoginBean;
import com.poly.java5.Entity.User;
import com.poly.java5.Service.UserService;
import com.poly.java5.Utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private UserService userService;

	@GetMapping("")
	public String loginUI(Model model) {
		model.addAttribute("bean", new LoginBean());
		return "login";
	}

	@PostMapping("")
	public String login(@Valid @ModelAttribute("bean") LoginBean loginBean, Errors errors, Model model,
			HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute("bean", loginBean);
		// Kiểm tra validation cơ bản
		if (errors.hasErrors()) {
			return "login";

		}
		// kiểm tra đăng nhập
		User user = userService.login(loginBean.getUsernameOrEmail(), loginBean.getPassword());

		if (user == null) {
			errors.rejectValue("usernameOrEmail", "login.failed", "Tên đăng nhập/email hoặc mật khẩu không đúng");
			return "login";
		}
		// (tuỳ chọn) lưu Session 
		Utils.setUserSession(user.getId(), request);
		
		// ===== KIỂM TRA SESSION =====
		  System.out.println(">>> LOGIN SUCCESS | USER_ID = " + user.getId());
		    System.out.println(">>> ROLE = " + user.getRole());

		    // PHÂN QUYỀN
		    switch (user.getRole()) {
		        case ADMIN:
		            return "redirect:/admin";

		        case SELLER:
		            return "redirect:/seller";

		        default:
		            return "redirect:/home";
		    }
	}
}
