package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.vo.Odds;
import java.util.List;
import java.util.Map;

public interface SportsDataProvider {
    List<Event> fetchUpcomingFixtures(String league);
    Map<String, List<Odds>> fetchBestOdds(String league);
    Map<String, Integer[]> fetchScores(String league);
}