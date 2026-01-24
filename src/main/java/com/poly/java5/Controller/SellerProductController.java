package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller/product")
public class SellerProductController {

    @GetMapping("/add")
    public String add() {
        return "seller/product/add";
    }
}
