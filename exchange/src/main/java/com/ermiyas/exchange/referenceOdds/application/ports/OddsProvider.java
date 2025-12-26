package com.ermiyas.exchange.referenceOdds.application.ports;

import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.MarketType;
import com.ermiyas.exchange.referenceOdds.domain.ReferenceOddsSnapshot;

public interface OddsProvider {
    ReferenceOddsSnapshot fetchOdds(Fixture fixture, MarketType market);
}
