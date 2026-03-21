package net.est.uxagile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.RideDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.RideService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/estimate")
    public RideDtos.EstimateResponse estimate(@Valid @RequestBody RideDtos.EstimateRequest request) {
        return rideService.estimate(request);
    }

    @PostMapping
    public RideDtos.RideResponse create(@Valid @RequestBody RideDtos.CreateRideRequest request) {
        return rideService.createRide(SecurityUtils.currentUserEmail(), request);
    }

    @GetMapping("/{id}")
    public RideDtos.RideResponse byId(@PathVariable Long id) {
        return rideService.getRideForPassenger(id, SecurityUtils.currentUserEmail());
    }

    @GetMapping("/{id}/status")
    public RideDtos.RideStatusResponse status(@PathVariable Long id) {
        return rideService.getRideStatusForPassenger(id, SecurityUtils.currentUserEmail());
    }

    @GetMapping("/{id}/tracking")
    public RideDtos.TrackingResponse tracking(@PathVariable Long id) {
        return rideService.getTracking(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/driver-location")
    public RideDtos.RideStatusResponse driverLocation(@PathVariable Long id,
                                                      @Valid @RequestBody RideDtos.DriverLocationRequest request) {
        return rideService.updateDriverLocationByDriver(id, SecurityUtils.currentUserEmail(), request);
    }

    @PostMapping("/{id}/trigger-safety-check")
    public RideDtos.RideStatusResponse triggerSafety(@PathVariable Long id) {
        return rideService.triggerSafetyCheck(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/validate-driver")
    public RideDtos.RideStatusResponse validate(@PathVariable Long id) {
        return rideService.validateDriver(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/refuse-driver")
    public RideDtos.RideStatusResponse refuse(@PathVariable Long id) {
        return rideService.refuseDriver(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/start")
    public RideDtos.RideStatusResponse start(@PathVariable Long id) {
        return rideService.startRide(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/arrive")
    public RideDtos.RideStatusResponse arrive(@PathVariable Long id) {
        return rideService.arriveRide(id, SecurityUtils.currentUserEmail());
    }

    @PostMapping("/{id}/complete")
    public RideDtos.RideStatusResponse complete(@PathVariable Long id) {
        return rideService.completeRide(id, SecurityUtils.currentUserEmail());
    }
}
