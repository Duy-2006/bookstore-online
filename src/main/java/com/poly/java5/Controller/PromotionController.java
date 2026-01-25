package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Entity.Promotion;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/promotions")
public class PromotionController {

    @GetMapping("/create")
    public String createPromotionForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        return "promotion/create";
    }

    @PostMapping("/save")
    public String savePromotion(@Valid @ModelAttribute Promotion promotion, BindingResult result) {
        if (result.hasErrors()) {
            return "promotion/create";
        }
        // Chưa logic: Lưu DB
        return "redirect:/promotions/list";
    }

    @GetMapping("/list")
    public String listPromotions() {
        return "promotion/list";
    }
}