package com.ermiyas.exchange.referenceOdds.domain;
import com.ermiyas.exchange.common.odds;

import java.util.Map;
import java.time.Instant;
import java.util.Objects;

public final class ReferenceOddsSnapshot{
    private final String externalEventId; //OddsApi event id
    private final String provider;
    private final Map<Outcome,Odds> odds;
    private final Instant fetchedAt;

    public ReferenceOddsSnapshot(
            String externalEventId,
            String provider,
            Map<Outcome,Odds> odds,
            Instant fetchedAt
    ){
        this.externalEventId=Objects.requireNonNull(externalEventId);
        this.fetchedAt=Objects.requireNonNull(fetchedAt);
        this.odds=Map.copyOf(Objects.requireNonNull(odds));
        this.provider=Objects.requireNonNull(provider);
    }
    public String externalEventId(){
        return externalEventId;

    }
    public String provider(){
        return provider;
    }
    public Map<Outcome,Odds> odds(){
        return odds;
    }
    public Instant fetchedAt(){
        return fetchedAt;
    }


}


