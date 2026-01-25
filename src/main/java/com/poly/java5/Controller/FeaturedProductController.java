package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Entity.FeaturedProduct;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/featured")
public class FeaturedProductController {

    @GetMapping("/add")
    public String addFeaturedForm(Model model) {
        model.addAttribute("featuredProduct", new FeaturedProduct());
        return "featured/add";
    }

    @PostMapping("/save")
    public String saveFeatured(@Valid @ModelAttribute FeaturedProduct featuredProduct, BindingResult result) {
        if (result.hasErrors()) {
            return "featured/add";
        }
        // Chưa logic: Cập nhật DB
        return "redirect:/featured/list";
    }

    @GetMapping("/list")
    public String listFeatured() {
        return "featured/list";
    }
}
