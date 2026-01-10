package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.vo.Odds;
import lombok.Value;

@Value // Immutable data carrier
public class BestOddsResult {
    Odds homeOdds;
    String homeSource;
    Odds awayOdds;
    String awaySource;
    Odds drawOdds;
    String drawSource;
}