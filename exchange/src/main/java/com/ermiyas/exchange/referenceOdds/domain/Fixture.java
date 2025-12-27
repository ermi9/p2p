package com.ermiyas.exchange.referenceOdds.domain;

import java.time.LocalDateTime;

public record Fixture(
    String id,
    String homeTeam,
    String awayTeam,
    LocalDateTime startTime,
    League league
) {}