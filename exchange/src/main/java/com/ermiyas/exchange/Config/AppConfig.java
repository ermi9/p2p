package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.vo.*;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommissionPolicy commissionPolicy() throws ExchangeException {
        // FIX: Use the concrete implementation class
        return new StandardPercentagePolicy(new BigDecimal("0.05")); 
    }
}