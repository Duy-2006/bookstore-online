package com.fpoly.java5demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DuAnBanSachApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DuAnBanSachApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DuAnBanSachApplication.class, args);
    }
}

