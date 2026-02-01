package com.ermiyas.exchange.infrastructure.sports;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * FixtureNameMatcher (OCP Friendly)
 * This class is now "closed for modification" but "open for extension".
 *
 */
@Component
public class FixtureNameMatcher {

    private final List<NameMatchingStrategy> strategies;

    public FixtureNameMatcher(List<NameMatchingStrategy> strategies) {
        this.strategies = strategies;
    }


    public boolean namesMatch(String nameA, String nameB) {
        if (nameA == null || nameB == null) {
            return false;
        }
        
        for (NameMatchingStrategy strategy : strategies) {
            if (strategy.matches(nameA, nameB)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Strategy interface for different matching algorithms.
     */
    public interface NameMatchingStrategy {
        boolean matches(String nameA, String nameB);
    }

    /**
     *  Contains our "contains" logic.
     */
    @Component
    public static class DefaultContainsMatcher implements NameMatchingStrategy {
        @Override
        public boolean matches(String nameA, String nameB) {
            String cleanA = nameA.toLowerCase().trim();
            String cleanB = nameB.toLowerCase().trim();
            return cleanA.contains(cleanB) || cleanB.contains(cleanA);
        }
    }
}