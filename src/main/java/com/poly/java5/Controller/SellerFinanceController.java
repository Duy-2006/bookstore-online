package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller/finance")
public class SellerFinanceController {

    @GetMapping("")
    public String report() {
        return "finance/report";
    }
}
//push code