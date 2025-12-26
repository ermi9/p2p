package com.ermiyas.exchange.referenceOdds.infrastructure;

import io.github.cdimascio.dotenv.Dotenv;

public final class OddsApiConfig {

    private static final Dotenv dotenv = Dotenv.load();

    private OddsApiConfig() {}

    public static String getApiKey() {
        return dotenv.get("ODDS_API_KEY");
    }

    public static String getHost() {
        return dotenv.get("ODDS_API_HOST");
    }
}
