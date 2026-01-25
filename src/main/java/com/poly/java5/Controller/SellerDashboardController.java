package com.poly.java5.Controller;

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
//    push code
}
