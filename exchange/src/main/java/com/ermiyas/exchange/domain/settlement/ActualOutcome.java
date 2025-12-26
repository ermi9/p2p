package com.ermiyas.exchange.domain.settlement;

import java.time.Instant;
import java.util.Objects;

public final class ActualOutcome {
    private final String winningOutcome;
    private final Instant decidedAt;

    public ActualOutcome( String winningOutcome, Instant decidedAt) {
        this.winningOutcome = Objects.requireNonNull(winningOutcome);
        if(winningOutcome.isBlank())
            throw new IllegalArgumentException("winningOutcome cannot be blank");
        this.decidedAt = Objects.requireNonNull(decidedAt);

    }

    public String winningOutcome() { return winningOutcome; }
    public Instant decidedAt() { return decidedAt; }

    public static ActualOutcome now(String winningOutcome) {
        return new ActualOutcome(winningOutcome, Instant.now());
    }
}
