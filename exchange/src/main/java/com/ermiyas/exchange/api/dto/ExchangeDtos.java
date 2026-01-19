package com.ermiyas.exchange.api.dto;

import com.ermiyas.exchange.domain.model.Outcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExchangeDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOfferRequest {
        private Long makerId;
        private Long eventId;
        private Outcome outcome;
        private BigDecimal odds;
        private BigDecimal stake;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchBetRequest {
        private Long takerId;
        private Long offerId;
        private BigDecimal amountToMatch;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WalletActionRequest {
        private BigDecimal amount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminSettleRequest {
        private List<String> externalIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
    }

    //Dashboard Statistics DTO
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DashboardStatsResponse {
        private long totalUsers;
        private long activeFixtures;
        private java.math.BigDecimal lockedStake;
    }

    // Standard Login Request
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
        private String errorCode;
        private long timestamp;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class EventSummaryResponse {
        private Long id;
        private String externalId; // Correctly mapped
        private String homeTeam;
        private String awayTeam;
        private LocalDateTime startTime;
        private String leagueName;
        private Double homeOdds;
        private Double awayOdds;
        private Double drawOdds;
        private String homeSource;
        private String awaySource;
        private String drawSource;

        private String status;
        private Integer offerCount;
        private Integer finalHomeScore;
        private Integer finalAwayScore;
    }

    @Data
    @Builder
    public static class UserResponse {
        private Long id;
        private String username;
        private String role;
    }

    @Data
    @Builder
    public static class EventResponse {
        private Long id;
        private String homeTeam;
        private String awayTeam;
        private LocalDateTime startTime;
        private String leagueName; // Added to match Markets logic
    }

    @Data
    @Builder
    public static class OfferResponse {
        private Long id;
        private UserResponse maker;
        private EventResponse event;
        private String outcome;
        private BigDecimal odds;
        private BigDecimal remainingStake;
        private String status;
    }

    @Data
    @Builder
    public static class MatchedBetResponse {
        private Long id;
        private OfferResponse offer;
        private UserResponse taker;
        private BigDecimal takerLiability;
        private BigDecimal makerStake;
        private BigDecimal odds;
        private String status;
    }
}
