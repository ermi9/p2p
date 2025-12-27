package com.ermiyas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // Add this line here
public class ExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeApplication.class, args);
    }
}