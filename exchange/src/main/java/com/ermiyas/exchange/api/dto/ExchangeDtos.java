package com.ermiyas.exchange.api.dto;

import com.ermiyas.exchange.domain.model.Outcome;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request/Response DTOs.
 * These decouple the API from the internal Domain Entities.
 */
public class ExchangeDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOfferRequest {
        private Long eventId;
        private Outcome outcome;
        private BigDecimal odds;
        private BigDecimal stake;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchBetRequest {
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
    public static class EventSummaryResponse {
        private Long id;
        private String homeTeam;
        private String awayTeam;
        private LocalDateTime startTime;
        private String leagueName;
        private Double homeOdds;
        private Double awayOdds;
        private Double drawOdds;
    }
}