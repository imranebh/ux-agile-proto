package net.est.uxagile.mapper;

import net.est.uxagile.domain.model.DriverLocation;
import net.est.uxagile.domain.model.Ride;
import net.est.uxagile.dto.RideDtos;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RideMapper {
    public RideDtos.RideResponse toResponse(Ride ride) {
        RideDtos.RideResponse response = new RideDtos.RideResponse();
        response.setId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setSafetyStatus(ride.getSafetyStatus());
        response.setPickupAddress(ride.getPickupAddress());
        response.setDestinationAddress(ride.getDestinationAddress());
        response.setEstimatedPrice(ride.getEstimatedPrice());
        response.setFinalPrice(ride.getFinalPrice());
        response.setDistanceKm(ride.getDistanceKm());
        response.setDurationMinutes(ride.getDurationMinutes());
        response.setPassengerLocationUnlocked(ride.getPassengerLocationUnlocked());
        response.setDriver(toDriverProfile(ride));
        response.setUpdatedAt(ride.getUpdatedAt());
        response.setCompletedAt(ride.getCompletedAt());
        return response;
    }

    public RideDtos.RideStatusResponse toStatus(Ride ride) {
        RideDtos.RideStatusResponse response = new RideDtos.RideStatusResponse();
        response.setRideId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setSafetyStatus(ride.getSafetyStatus());
        response.setPassengerLocationUnlocked(ride.getPassengerLocationUnlocked());
        response.setDriver(toDriverProfile(ride));
        return response;
    }

    public RideDtos.TrackingResponse toTracking(Ride ride, List<DriverLocation> locations) {
        RideDtos.TrackingResponse response = new RideDtos.TrackingResponse();
        response.setRideId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setSafetyStatus(ride.getSafetyStatus());
        response.setPassengerLocationUnlocked(ride.getPassengerLocationUnlocked());
        response.setDriver(toDriverProfile(ride));
        response.setPickupLat(Boolean.TRUE.equals(ride.getPassengerLocationUnlocked()) ? ride.getPickupLat() : null);
        response.setPickupLng(Boolean.TRUE.equals(ride.getPassengerLocationUnlocked()) ? ride.getPickupLng() : null);
        response.setDestinationLat(ride.getDestinationLat());
        response.setDestinationLng(ride.getDestinationLng());
        response.setLocations(locations.stream().map(this::toView).toList());
        return response;
    }

    private RideDtos.DriverProfile toDriverProfile(Ride ride) {
        if (ride.getDriver() == null || ride.getDriver().getUser() == null || ride.getVehicle() == null) {
            return null;
        }
        RideDtos.DriverProfile profile = new RideDtos.DriverProfile();
        profile.setDriverId(ride.getDriver().getId());
        profile.setFullName(ride.getDriver().getUser().getFullName());
        profile.setPhotoUrl(ride.getDriver().getPhotoUrl());
        profile.setPlateNumber(ride.getVehicle().getPlateNumber());
        profile.setVehicleModel(ride.getVehicle().getModel());
        profile.setVehicleColor(ride.getVehicle().getColor());
        profile.setRating(ride.getDriver().getRating());
        return profile;
    }

    private RideDtos.DriverLocationView toView(DriverLocation location) {
        RideDtos.DriverLocationView view = new RideDtos.DriverLocationView();
        view.setLatitude(location.getLatitude());
        view.setLongitude(location.getLongitude());
        view.setDistanceToPickupMeters(location.getDistanceToPickupMeters());
        view.setTimestamp(location.getTimestamp());
        return view;
    }
}
