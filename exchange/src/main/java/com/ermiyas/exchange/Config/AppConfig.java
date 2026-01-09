package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.StandardPercentagePolicy;
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

    /**
     * Requirement: Provide the concrete strategy for commission.
     * Fixed: Returns StandardPercentagePolicy instead of trying to instantiate the interface.
     */
    @Bean
    public CommissionPolicy commissionPolicy() throws ExchangeException {
        // Using 0.05 for a 5% commission rate
        return new StandardPercentagePolicy(new BigDecimal("0.05"));
    }
}