package net.est.uxagile.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    public BigDecimal calculatePrice(BigDecimal distanceKm, int durationMinutes) {
        BigDecimal base = BigDecimal.valueOf(2.00);
        BigDecimal distancePart = distanceKm.multiply(BigDecimal.valueOf(1.50));
        BigDecimal timePart = BigDecimal.valueOf(durationMinutes).multiply(BigDecimal.valueOf(0.25));
        return base.add(distancePart).add(timePart).setScale(2, RoundingMode.HALF_UP);
    }
}
