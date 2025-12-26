package com.ermiyas.exchange.referenceOdds.domain;

public enum League {

    EPL("soccer_epl", "Premier League"),
    LA_LIGA("soccer_spain_la_liga", "La Liga"),
    SERIE_A("soccer_italy_serie_a", "Serie A"),
    BUNDESLIGA("soccer_germany_bundesliga", "Bundesliga"),
    LIGUE_1("soccer_france_ligue_one", "Ligue 1");

    private final String oddsApiKey;
    private final String displayName;

    League(String oddsApiKey, String displayName) {
        this.oddsApiKey = oddsApiKey;
        this.displayName = displayName;
    }

    /** Key used when calling the Odds API */
    public String oddsApiKey() {
        return oddsApiKey;
    }

    /** Human-readable name (UI, logs, admin tools) */
    public String displayName() {
        return displayName;
    }
}
