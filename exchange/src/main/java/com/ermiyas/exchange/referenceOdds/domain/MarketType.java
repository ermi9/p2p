package com.ermiyas.exchange.referenceOdds.domain;

public final class MarketType {

    public static final MarketType H2H = new MarketType("H2H");

    private final String code;

    private MarketType(String code) {
        this.code = code;
    }

    public static MarketType of(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("MarketType code cannot be blank");
        }
        return new MarketType(code);
    }

    public String code() {
        return code;
    }
}
