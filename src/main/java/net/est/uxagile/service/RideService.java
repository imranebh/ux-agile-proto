package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.domain.enums.SafetyStatus;
import net.est.uxagile.domain.model.*;
import net.est.uxagile.dto.RideDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.integration.MapsProvider;
import net.est.uxagile.mapper.RideMapper;
import net.est.uxagile.realtime.RealtimeTrackingService;
import net.est.uxagile.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverLocationRepository driverLocationRepository;
    private final SOSIncidentRepository sosIncidentRepository;
    private final MapsProvider mapsProvider;
    private final PricingService pricingService;
    private final RideMapper rideMapper;
    private final RealtimeTrackingService realtimeTrackingService;

    public RideDtos.EstimateResponse estimate(RideDtos.EstimateRequest request) {
        MapsProvider.Estimate estimate = mapsProvider.estimate(
                request.getPickupLat(), request.getPickupLng(), request.getDestinationLat(), request.getDestinationLng());
        RideDtos.EstimateResponse response = new RideDtos.EstimateResponse();
        response.setDistanceKm(estimate.distanceKm());
        response.setDurationMinutes(estimate.durationMinutes());
        response.setEstimatedPrice(pricingService.calculatePrice(estimate.distanceKm(), estimate.durationMinutes()));
        return response;
    }

    @Transactional
    public RideDtos.RideResponse createRide(String email, RideDtos.CreateRideRequest request) {
        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Passenger not found"));

        var estimate = mapsProvider.estimate(request.getPickupLat(), request.getPickupLng(), request.getDestinationLat(), request.getDestinationLng());

        Driver driver = driverRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No driver available"));
        Vehicle vehicle = vehicleRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No vehicle available"));

        Ride ride = new Ride();
        ride.setPassenger(passenger);
        ride.setDriver(driver);
        ride.setVehicle(vehicle);
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDestinationAddress(request.getDestinationAddress());
        ride.setPickupLat(request.getPickupLat());
        ride.setPickupLng(request.getPickupLng());
        ride.setDestinationLat(request.getDestinationLat());
        ride.setDestinationLng(request.getDestinationLng());
        ride.setDistanceKm(estimate.distanceKm());
        ride.setDurationMinutes(estimate.durationMinutes());
        BigDecimal calculatedPrice = pricingService.calculatePrice(estimate.distanceKm(), estimate.durationMinutes());
        ride.setEstimatedPrice(calculatedPrice);
        ride.setFinalPrice(calculatedPrice);
        ride.setStatus(RideStatus.DRIVER_ASSIGNED);
        ride.setUpdatedAt(Instant.now());
        ride = rideRepository.save(ride);
        transitionTo(ride, RideStatus.DRIVER_EN_ROUTE, SafetyStatus.NORMAL);
        return rideMapper.toResponse(ride);
    }

    public Ride findRideOrThrow(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ride not found"));
    }

    public RideDtos.RideResponse getRideForPassenger(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        return rideMapper.toResponse(ride);
    }

    public RideDtos.RideStatusResponse getRideStatusForPassenger(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        return rideMapper.toStatus(ride);
    }

    public RideDtos.TrackingResponse getTracking(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        return rideMapper.toTracking(ride, driverLocationRepository.findByRideOrderByTimestampAsc(ride));
    }

    public SseEmitter subscribeForPassenger(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        return realtimeTrackingService.subscribe(rideId);
    }

    @Transactional
    public RideDtos.RideStatusResponse updateDriverLocationByDriver(Long rideId, String email, RideDtos.DriverLocationRequest request) {
        Ride ride = findRideOrThrow(rideId);
        ensureDriverOwnership(ride, email);
        return updateDriverLocationInternal(ride, request);
    }

    @Transactional
    public RideDtos.RideStatusResponse updateDriverLocationFromSystem(Long rideId, RideDtos.DriverLocationRequest request) {
        Ride ride = findRideOrThrow(rideId);
        return updateDriverLocationInternal(ride, request);
    }

    private RideDtos.RideStatusResponse updateDriverLocationInternal(Ride ride, RideDtos.DriverLocationRequest request) {
        if (ride.getDriver() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No driver assigned");
        }

        DriverLocation location = new DriverLocation();
        location.setRide(ride);
        location.setDriver(ride.getDriver());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        double dist = distanceMeters(request.getLatitude(), request.getLongitude(), ride.getPickupLat(), ride.getPickupLng());
        location.setDistanceToPickupMeters(dist);
        driverLocationRepository.save(location);

        if (dist <= 50 && ride.getStatus() == RideStatus.DRIVER_EN_ROUTE) {
            transitionTo(ride, RideStatus.SAFETY_GATE_TRIGGERED, SafetyStatus.WITHIN_50M);
            transitionTo(ride, RideStatus.AWAITING_PASSENGER_DECISION, SafetyStatus.PROFILE_SHOWN);
        }

        realtimeTrackingService.publishDriverLocation(ride.getId(), rideMapper.toTracking(ride, List.of(location)));
        return rideMapper.toStatus(rideRepository.save(ride));
    }

    @Transactional
    public RideDtos.RideStatusResponse triggerSafetyCheck(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.DRIVER_EN_ROUTE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Safety check can only trigger while driver is en route");
        }
        transitionTo(ride, RideStatus.SAFETY_GATE_TRIGGERED, SafetyStatus.WITHIN_50M);
        transitionTo(ride, RideStatus.AWAITING_PASSENGER_DECISION, SafetyStatus.PROFILE_SHOWN);
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    @Transactional
    public RideDtos.RideStatusResponse validateDriver(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.AWAITING_PASSENGER_DECISION) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ride is not awaiting passenger decision");
        }
        ride.setPassengerLocationUnlocked(true);
        transitionTo(ride, RideStatus.APPROVED_TO_APPROACH, SafetyStatus.VALIDATED);
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    @Transactional
    public RideDtos.RideStatusResponse refuseDriver(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.AWAITING_PASSENGER_DECISION) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ride is not awaiting passenger decision");
        }
        ride.setEstimatedPrice(BigDecimal.ZERO);
        ride.setFinalPrice(BigDecimal.ZERO);
        transitionTo(ride, RideStatus.CANCELED_BY_REFUSAL, SafetyStatus.REFUSED);

        SOSIncident incident = new SOSIncident();
        incident.setRide(ride);
        incident.setUser(ride.getPassenger());
        incident.setReason("Passenger refused driver at safety gate");
        sosIncidentRepository.save(incident);

        ride.setSafetyStatus(SafetyStatus.SOS_TRIGGERED);
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    @Transactional
    public RideDtos.RideStatusResponse startRide(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.APPROVED_TO_APPROACH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ride cannot start before validation");
        }
        transitionTo(ride, RideStatus.IN_PROGRESS, ride.getSafetyStatus());
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    @Transactional
    public RideDtos.RideStatusResponse arriveRide(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ride must be in progress");
        }
        transitionTo(ride, RideStatus.ARRIVED, ride.getSafetyStatus());
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    @Transactional
    public RideDtos.RideStatusResponse completeRide(Long rideId, String email) {
        Ride ride = findRideOrThrow(rideId);
        ensurePassengerOwnership(ride, email);
        if (ride.getStatus() != RideStatus.ARRIVED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ride must be arrived before completion");
        }
        transitionTo(ride, RideStatus.COMPLETED, ride.getSafetyStatus());
        ride.setCompletedAt(Instant.now());
        rideRepository.save(ride);
        return rideMapper.toStatus(ride);
    }

    private void ensurePassengerOwnership(Ride ride, String email) {
        if (!ride.getPassenger().getEmail().equalsIgnoreCase(email)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied for this ride");
        }
    }

    private void ensureDriverOwnership(Ride ride, String email) {
        if (ride.getDriver() == null || ride.getDriver().getUser() == null ||
                !ride.getDriver().getUser().getEmail().equalsIgnoreCase(email)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only the assigned driver can update location");
        }
    }

    private void transitionTo(Ride ride, RideStatus status, SafetyStatus safetyStatus) {
        ride.setStatus(status);
        ride.setSafetyStatus(safetyStatus);
        ride.setUpdatedAt(Instant.now());
        realtimeTrackingService.publishRideUpdate(ride.getId(), rideMapper.toStatus(ride));
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = (lat1 - lat2) * 111_000;
        double dLon = (lon1 - lon2) * 111_000;
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }
}
