package com.ermiyas.exchange.Config;

import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Configuration
public class AppConfig {

    /**
     * Required for TheOddsApiClient to make HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Requirement: Set the platform commission (e.g., 5%).
     * Only applied to the net profit of winners.
     */
    @Bean
    public CommissionPolicy commissionPolicy() {
        return new CommissionPolicy(new BigDecimal("0.05"));
    }
}