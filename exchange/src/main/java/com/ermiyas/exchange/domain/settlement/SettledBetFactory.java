package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.domain.offer.Position;
import com.ermiyas.exchange.domain.orderbook.BetAgreement;

/**
 * Factory was empty; now converts agreements into domain objects.
 */
public final class SettledBetFactory {
    private SettledBetFactory() {
    }

    public static SettledBet from(BetAgreement agreement) {
        Position position = agreement.position();
        if (position == Position.FOR) {
            return new ForSettledBet(
                    agreement.makerUserId(),
                    agreement.takerUserId(),
                    agreement.amount(),
                    agreement.odds()
            );
        }
        return new AgainstSettledBet(
                agreement.makerUserId(),
                agreement.takerUserId(),
                agreement.amount(),
                agreement.odds()
        );
    }
}
