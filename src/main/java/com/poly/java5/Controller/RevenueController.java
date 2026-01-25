package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.poly.java5.Entity.Commission;
import com.poly.java5.Entity.Revenue;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/revenues")
public class RevenueController {

    @GetMapping
    public String listRevenues(Model model) {
        // Chưa logic: Lấy list từ DB
        model.addAttribute("revenues", new Revenue()); // Placeholder
        return "revenue/list";
    }

    @GetMapping("/add")
    public String addRevenueForm(Model model) {
        model.addAttribute("revenue", new Revenue());
        model.addAttribute("commission", new Commission());
        return "revenue/add";
    }

    @PostMapping("/save")
    public String saveRevenue(@Valid @ModelAttribute Revenue revenue, BindingResult result) {
        if (result.hasErrors()) {
            return "revenue/add";
        }
        // Chưa logic: Lưu DB
        return "redirect:/revenues";
    }
}
