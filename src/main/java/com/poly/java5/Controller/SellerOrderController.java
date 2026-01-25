package com.poly.java5.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller/order")
public class SellerOrderController {

    @GetMapping("")
    public String list() {
        return "seller/order/list";
    }
}
//push code