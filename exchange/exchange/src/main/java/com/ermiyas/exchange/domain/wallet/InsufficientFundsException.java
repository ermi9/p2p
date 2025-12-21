package com.ermiyas.exchange.domain.wallet;

import com.ermiyas.exchange.common.Money;
import java.util.Objects;

/**
 * Provides a descriptive runtime error instead of bare IllegalArgumentException.
 */
public final class InsufficientFundsException extends WalletException {
    private final Money available;
    private final Money required;

    public InsufficientFundsException(Money available, Money required) {
        super(buildMessage(available,required));
        this.available=Objects.requireNonNull(available,"available must not be null");
        this.required=Objects.requireNonNull(required,"required must not be null");

}
private static String buildMessage(Money available, Money required){
    String a=(available==null) ? "null":available.value().toPlainString();
    String b=(required==null) ? "null": required.value().toPlainString();
    return "Insufficient funds: available"+ a + " required= " +b;
}
public Money available(){
    return available;
}
public Money required(){
 return required;   
}
}
