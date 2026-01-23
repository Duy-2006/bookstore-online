package com.fpoly.java5demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller/promotion")
public class SellerPromotionController {

    @GetMapping("/add")
    public String add() {
        return "seller/promotion/add";
    }
}
