package net.est.uxagile.service;

import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.dto.RideDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SafetyFlowServiceTest {

    @Autowired
    private RideService rideService;

    @Test
    void shouldTriggerValidateAndRefuseSafetyFlow() {
        RideDtos.RideStatusResponse triggered = rideService.triggerSafetyCheck(1L, "passenger@autostop.dev");
        assertEquals(RideStatus.AWAITING_PASSENGER_DECISION, triggered.getStatus());

        RideDtos.RideStatusResponse validated = rideService.validateDriver(1L, "passenger@autostop.dev");
        assertEquals(RideStatus.APPROVED_TO_APPROACH, validated.getStatus());

        rideService.startRide(1L, "passenger@autostop.dev");
        rideService.arriveRide(1L, "passenger@autostop.dev");
        rideService.completeRide(1L, "passenger@autostop.dev");

        RideDtos.CreateRideRequest secondRide = new RideDtos.CreateRideRequest();
        secondRide.setPickupAddress("A");
        secondRide.setDestinationAddress("B");
        secondRide.setPickupLat(3.848);
        secondRide.setPickupLng(11.501);
        secondRide.setDestinationLat(3.860);
        secondRide.setDestinationLng(11.510);
        RideDtos.RideResponse created = rideService.createRide("passenger@autostop.dev", secondRide);

        rideService.triggerSafetyCheck(created.getId(), "passenger@autostop.dev");
        RideDtos.RideStatusResponse refused = rideService.refuseDriver(created.getId(), "passenger@autostop.dev");
        assertEquals(RideStatus.CANCELED_BY_REFUSAL, refused.getStatus());
    }
}
