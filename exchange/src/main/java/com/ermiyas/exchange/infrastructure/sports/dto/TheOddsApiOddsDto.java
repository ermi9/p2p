package com.ermiyas.exchange.infrastructure.sports.dto;

import lombok.Data;
import java.util.List;

@Data
public class TheOddsApiOddsDto {
    private String id;
    private String sport_title;
    private String home_team;
    private String away_team;
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