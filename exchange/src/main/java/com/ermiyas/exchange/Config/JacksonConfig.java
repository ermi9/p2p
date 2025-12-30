package com.ermiyas.exchange.Config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        // This module tells Jackson to ignore the Hibernate internal proxies
        // and only serialize the actual data.
        return new Hibernate6Module();
    }
}