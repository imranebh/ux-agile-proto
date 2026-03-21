package net.est.uxagile.integration;

import java.math.BigDecimal;
import java.util.List;

public interface MapsProvider {

    List<String> autocomplete(String query);

    Estimate estimate(double fromLat, double fromLng, double toLat, double toLng);

    record Estimate(BigDecimal distanceKm, int durationMinutes) {
    }
}
