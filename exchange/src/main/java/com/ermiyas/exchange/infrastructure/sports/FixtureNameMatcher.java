package com.ermiyas.exchange.infrastructure.sports;

import org.springframework.stereotype.Component;

@Component
public class FixtureNameMatcher {

    /**
     * Requirement: Contains logic for name matching.
     * Example: "Arsenal" matches "Arsenal FC"
     */
    public boolean namesMatch(String nameA, String nameB) {
        if (nameA == null || nameB == null) return false;
        
        String cleanA = nameA.toLowerCase().trim();
        String cleanB = nameB.toLowerCase().trim();

        return cleanA.contains(cleanB) || cleanB.contains(cleanA);
    }
}