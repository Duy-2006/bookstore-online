package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Service.BannerService;
import com.poly.java5.Service.BookService;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {
	private final BookService bookService;
	private final BannerService bannerService; // ✅ inject


	    @GetMapping("")
	    public String home(Model model) {

	        model.addAttribute("banners", bannerService.getActiveBanners());
	        model.addAttribute("newBooks", bookService.getNewBooks());

	        return "home";
	    }
}
