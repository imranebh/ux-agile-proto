package net.est.uxagile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import net.est.uxagile.domain.enums.PaymentStatus;

import java.math.BigDecimal;

public class PaymentDtos {
    @Data
    public static class AddPaymentMethodRequest {
        @NotBlank
        private String provider;
        @NotBlank
        private String brand;
        @NotBlank
        @Pattern(regexp = "\\d{12,19}")
        private String cardNumber;
        @NotBlank
        @Pattern(regexp = "0[1-9]|1[0-2]")
        private String expMonth;
        @NotBlank
        @Pattern(regexp = "\\d{2,4}")
        private String expYear;
        @NotBlank
        @Pattern(regexp = "\\d{3,4}")
        private String cvv;
    }

    @Data
    public static class PaymentMethodResponse {
        private Long id;
        private String provider;
        private String brand;
        private String last4;
        private String expiryMonth;
        private String expiryYear;
        private Boolean defaultMethod;
    }

    @Data
    public static class PaymentResponse {
        private Long rideId;
        private BigDecimal amount;
        private PaymentStatus status;
        private String reference;
    }
}
