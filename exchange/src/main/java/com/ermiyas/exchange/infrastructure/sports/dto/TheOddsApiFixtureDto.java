package com.ermiyas.exchange.infrastructure.sports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TheOddsApiFixtureDto {
    private String id;
    @JsonProperty("home_team")
    private String homeTeam;
    @JsonProperty("away_team")
    private String awayTeam;
    @JsonProperty("commence_time")
    private LocalDateTime startTime;
}