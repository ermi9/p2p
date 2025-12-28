package com.ermiyas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling; // 1. Add this import

@SpringBootApplication
@EnableCaching
@EnableScheduling // 2. Add this line to turn on your Crawler
public class ExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeApplication.class, args);
    }
}