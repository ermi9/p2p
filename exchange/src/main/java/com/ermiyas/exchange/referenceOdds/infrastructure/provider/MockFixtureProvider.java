package com.ermiyas.exchange.referenceOdds.infrastructure.provider;

import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.League;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public class MockFixtureProvider implements FixtureProvider {

    @Override
    public List<Fixture> fetchFixtures(League league) {
        // This returns "fake" data regardless of the API status
        return List.of(
            new Fixture(
                "mock-1",
                "Manchester City",
                "Arsenal",
                LocalDateTime.now().plusDays(1),
                league
            ),
            new Fixture(
                "mock-2",
                "Real Madrid",
                "Barcelona",
                LocalDateTime.now().plusDays(2),
                league
            )
        );
    }

    @Override
    public String getName() {
        return "MockProvider";
    }
}