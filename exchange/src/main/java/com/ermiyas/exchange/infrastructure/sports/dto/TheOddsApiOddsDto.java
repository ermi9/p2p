package com.ermiyas.exchange.infrastructure.sports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * REFACTORED: TheOddsApiOddsDto (OCP Friendly)
 */
@Data
public class TheOddsApiOddsDto {
    private String id;

    @JsonProperty("sport_title")
    private String sportTitle;

    @JsonProperty("home_team")
    private String homeTeam;

    @JsonProperty("away_team")
    private String awayTeam;

    private List<Bookmaker> bookmakers;

    @Data
    public static class Bookmaker {
        private String key;
        private List<Market> markets;
    }

    @Data
    public static class Market {
        private String key; // h2h
        private List<Outcome> outcomes;
    }

    @Data
    public static class Outcome {
        private String name;
        private double price;
    }
}