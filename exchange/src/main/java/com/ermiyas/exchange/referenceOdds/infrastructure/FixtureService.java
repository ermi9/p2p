package com.ermiyas.exchange.referenceOdds.infrastructure;

import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.League;
import com.ermiyas.exchange.referenceOdds.infrastructure.provider.FixtureProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
public class FixtureService {
    private final List<FixtureProvider> providers;

    public FixtureService(List<FixtureProvider> providers) {
        this.providers = providers;
    }

    // Fetches for a single league (existing logic)
    public List<Fixture> getAllFixtures(League league) {
        return providers.stream()
                .flatMap(p -> p.fetchFixtures(league).stream())
                .collect(Collectors.toList());
    }

    // NEW: Aggregates Top 5 Leagues
    @Cacheable(value = "fixtures", key = "'top5'")
    public List<Fixture> getTop5Leagues() {
        List<League> top5 = Arrays.asList(
            League.PL, League.PD, League.BL1, League.SA, League.FL1
        );

        // Fetch all 5 leagues at the same time
        List<CompletableFuture<List<Fixture>>> futures = top5.stream()
            .map(league -> CompletableFuture.supplyAsync(() -> getAllFixtures(league)))
            .toList();

        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}