package com.ermiyas.exchange.referenceOdds.domain;

import java.time.Instant;
import java.util.Objects;
/**
 * A fixture represents a scheduled football match
 * that users can post offers on
 * 
 * It is a read-only fact coming from an external source
 * (OddsApi, scrapers, crawlers, etc).
 */

public class Fixture {
    private final String externalEventId;
    private final String leagueKey;//soccer_epl,soccer_serie_a, etc
    private final String homeTeam;
    private final String awayTeam;
    private final Instant kickoffTime;

    public Fixture(
            String externalEventId,
            String leagueKey,
            String homeTeam,
            String awayTeam,
            Instant kickoffTime
    ){
        this.externalEventId=externalEventId;
        this.leagueKey=leagueKey;
        this.homeTeam=homeTeam;
        this.awayTeam=awayTeam;
        this.kickoffTime=kickoffTime;
    }
    public String externalEventId(){
        return externalEventId;
    }
    public String leagueKey(){
        return leagueKey;
    }
    public String homeTeam(){
        return homeTeam;
    }
    public String awayTeam(){
        return awayTeam;
    }
    public Instant kickoffTime(){
        return kickoffTime;
    }
    public boolean isUpcoming(Instant now){
        return kickoffTime.isAfter(now);
    }
}
