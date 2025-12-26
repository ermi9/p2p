package com.ermiyas.exchange.referenceOdds.domain;

import java.time.Instant;
import java.util.Objects;

public final class Fixture {

    private final String id;
    private final League league;
    private final String home;
    private final String away;
    private final Instant kickoff;

    public Fixture(String id, League league, String home, String away, Instant kickoff) {
        this.id = Objects.requireNonNull(id);
        this.league = Objects.requireNonNull(league);
        this.home = Objects.requireNonNull(home);
        this.away = Objects.requireNonNull(away);
        this.kickoff = Objects.requireNonNull(kickoff);
    }

    public String id() { return id; }
    public League league() { return league; }
    public String home() { return home; }
    public String away() { return away; }
    public Instant kickoff() { return kickoff; }
}
