package com.ermiyas.exchange.referenceOdds.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "betting")
public class FixtureApiConfig {
    private String fixtureUrl;
    private String fixtureKey;

    public String getFixtureUrl() { return fixtureUrl; }
    public void setFixtureUrl(String url) { this.fixtureUrl = url; }
    public String getFixtureKey() { return fixtureKey; }
    public void setFixtureKey(String key) { this.fixtureKey = key; }
}