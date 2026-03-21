package net.est.uxagile.integration;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Component
public class MockMapsProvider implements MapsProvider {

    @Override
    public List<String> autocomplete(String query) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT);
        List<String> addresses = List.of(
                "Boulevard Zerktouni, Casablanca",
                "Avenue Mohammed V, Rabat",
                "Hassan II Mosque, Casablanca",
                "Avenue Hassan II, Marrakech",
                "Place Rachidi, Casablanca",
                "Avenue des FAR, Tanger",
                "Agdal, Rabat",
                "Maarif, Casablanca",
                "Gueliz, Marrakech",
                "Quartier Californie, Casablanca"
        );
        return addresses.stream()
                .filter(address -> address.toLowerCase(Locale.ROOT).contains(q))
                .limit(5)
                .toList();
    }

    @Override
    public Estimate estimate(double fromLat, double fromLng, double toLat, double toLng) {
        double dLat = fromLat - toLat;
        double dLng = fromLng - toLng;
        double euclid = Math.sqrt((dLat * dLat) + (dLng * dLng)) * 111;
        BigDecimal distance = BigDecimal.valueOf(Math.max(euclid, 1.0)).setScale(2, RoundingMode.HALF_UP);
        int minutes = Math.max(5, (int) Math.round(distance.doubleValue() * 3));
        return new Estimate(distance, minutes);
    }
}
