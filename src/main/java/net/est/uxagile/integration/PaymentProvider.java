package net.est.uxagile.integration;

import java.math.BigDecimal;

public interface PaymentProvider {
    String tokenizeCard(String cardNumber, String expMonth, String expYear, String cvv);
    ChargeResult charge(String token, BigDecimal amount);

    record ChargeResult(boolean success, String reference) {
    }
}
