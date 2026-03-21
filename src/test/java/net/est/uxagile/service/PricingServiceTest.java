package net.est.uxagile.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void shouldCalculatePriceWithBaseDistanceAndTime() {
        BigDecimal result = pricingService.calculatePrice(BigDecimal.valueOf(10), 20);
        assertEquals(new BigDecimal("22.00"), result);
    }
}
