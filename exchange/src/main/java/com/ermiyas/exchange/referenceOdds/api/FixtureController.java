package com.ermiyas.exchange.referenceOdds.api;

import com.ermiyas.exchange.referenceOdds.infrastructure.FixtureService;
import com.ermiyas.exchange.referenceOdds.domain.Fixture;
import com.ermiyas.exchange.referenceOdds.domain.League;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fixtures")
public class FixtureController {

    private final FixtureService fixtureService;

    public FixtureController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    // 1. Static path must come first or be uniquely identified
    @GetMapping("/top-5")
    public List<Fixture> getTop5() {
        return fixtureService.getTop5Leagues();
    }

    // 2. The dynamic path should be specific
    @GetMapping("/league/{leagueCode}")
    public List<Fixture> getByLeague(@PathVariable String leagueCode) {
        return fixtureService.getAllFixtures(League.valueOf(leagueCode.toUpperCase()));
    }
}