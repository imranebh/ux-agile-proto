package net.est.uxagile.integration;

import net.est.uxagile.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Component
public class MockPaymentProvider implements PaymentProvider {
    @Override
    public String tokenizeCard(String cardNumber, String expMonth, String expYear, String cvv) {
        if (cardNumber == null || !cardNumber.matches("\\d{12,19}")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid card number");
        }
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid CVV");
        }
        int month = parseMonth(expMonth);
        int year = parseYear(expYear);
        YearMonth expiry = YearMonth.of(year, month);
        if (expiry.isBefore(YearMonth.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Card is expired");
        }
        return "tok_" + cardNumber.substring(cardNumber.length() - 4);
    }

    @Override
    public ChargeResult charge(String token, BigDecimal amount) {
        boolean success = token != null && token.startsWith("tok_") && amount.compareTo(BigDecimal.ZERO) >= 0;
        return new ChargeResult(success, "ch_" + UUID.randomUUID());
    }

    private int parseMonth(String expMonth) {
        if (expMonth == null || !expMonth.matches("0[1-9]|1[0-2]")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid expiration month");
        }
        return Integer.parseInt(expMonth);
    }

    private int parseYear(String expYear) {
        if (expYear == null || !expYear.matches("\\d{2,4}")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid expiration year");
        }
        return expYear.length() == 2 ? 2000 + Integer.parseInt(expYear) : Integer.parseInt(expYear);
    }
}
