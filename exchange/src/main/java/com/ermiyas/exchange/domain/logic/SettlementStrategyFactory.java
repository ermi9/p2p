package com.ermiyas.exchange.domain.logic;

import com.ermiyas.exchange.domain.model.MarketType;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy Factory.
 * This class acts as a lookup table. It maps a MarketType (like THREE_WAY)
 * to the actual logic class that knows how to determine the winner.
 */
@Component
public class SettlementStrategyFactory {

    // A simple Map to store our strategies
    private final Map<MarketType, SettlementStrategy> strategies = new HashMap<>();

    /**
     * Spring finds every class that implements SettlementStrategy (marked with @Component)
     * and passes them into this constructor as a list.
     */
    public SettlementStrategyFactory(List<SettlementStrategy> strategyList) {
        for (SettlementStrategy strategy : strategyList) {
            this.strategies.put(strategy.getMarketType(), strategy);
        }
    }

    /**
     * Returns the correct strategy based on the event's MarketType.
     */
    public SettlementStrategy getStrategy(MarketType type) {
        SettlementStrategy strategy = this.strategies.get(type);
        
        //If no strategy is found, we fall back to ThreeWay
        // prevents the system from crashing if a market type is misconfigured.
        if (strategy == null) {
            return new ThreeWaySettlementStrategy();
        }
        
        return strategy;
    }
}