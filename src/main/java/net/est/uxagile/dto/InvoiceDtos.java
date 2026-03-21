package net.est.uxagile.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

public class InvoiceDtos {
    @Data
    public static class InvoiceResponse {
        private Long rideId;
        private String invoiceNumber;
        private BigDecimal amount;
        private Instant issuedAt;
    }
}
