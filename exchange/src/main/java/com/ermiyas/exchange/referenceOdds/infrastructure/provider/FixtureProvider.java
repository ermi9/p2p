package com.ermiyas.exchange.referenceOdds.infrastructure.provider;

import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.League;
import java.util.List;

public interface FixtureProvider {
    List<Fixture> fetchFixtures(League league);
    String getName();
}