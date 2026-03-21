package net.est.uxagile.config;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.dto.RideDtos;
import net.est.uxagile.repository.DriverRepository;
import net.est.uxagile.repository.RideRepository;
import net.est.uxagile.service.RideService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "autostop.demo", name = "simulator-enabled", havingValue = "true")
public class DemoSimulator {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RideService rideService;

    @Scheduled(fixedDelay = 5000)
    public void moveDriverTowardPickup() {
        var rides = rideRepository.findByStatusIn(java.util.List.of(RideStatus.DRIVER_EN_ROUTE));
        for (var ride : rides) {
            if (ride.getDriver() == null) {
                continue;
            }
            double lat = ride.getDriver().getCurrentLat();
            double lng = ride.getDriver().getCurrentLng();
            double targetLat = ride.getPickupLat();
            double targetLng = ride.getPickupLng();

            ride.getDriver().setCurrentLat(lat + ((targetLat - lat) * 0.15));
            ride.getDriver().setCurrentLng(lng + ((targetLng - lng) * 0.15));
            driverRepository.save(ride.getDriver());

            RideDtos.DriverLocationRequest request = new RideDtos.DriverLocationRequest();
            request.setLatitude(ride.getDriver().getCurrentLat());
            request.setLongitude(ride.getDriver().getCurrentLng());
            rideService.updateDriverLocationFromSystem(ride.getId(), request);
        }
    }
}
