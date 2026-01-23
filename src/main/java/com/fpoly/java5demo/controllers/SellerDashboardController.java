package com.fpoly.java5demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerDashboardController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "seller/dashboard";
    }
}
