package net.est.uxagile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.domain.enums.SafetyStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class RideDtos {
    @Data
    public static class EstimateRequest {
        @NotNull
        private Double pickupLat;
        @NotNull
        private Double pickupLng;
        @NotNull
        private Double destinationLat;
        @NotNull
        private Double destinationLng;
    }

    @Data
    public static class EstimateResponse {
        private BigDecimal distanceKm;
        private Integer durationMinutes;
        private BigDecimal estimatedPrice;
    }

    @Data
    public static class CreateRideRequest {
        @NotBlank
        private String pickupAddress;
        @NotBlank
        private String destinationAddress;
        @NotNull
        private Double pickupLat;
        @NotNull
        private Double pickupLng;
        @NotNull
        private Double destinationLat;
        @NotNull
        private Double destinationLng;
    }

    @Data
    public static class RideResponse {
        private Long id;
        private RideStatus status;
        private SafetyStatus safetyStatus;
        private String pickupAddress;
        private String destinationAddress;
        private BigDecimal estimatedPrice;
        private BigDecimal finalPrice;
        private BigDecimal distanceKm;
        private Integer durationMinutes;
        private Boolean passengerLocationUnlocked;
        private DriverProfile driver;
        private Instant updatedAt;
        private Instant completedAt;
    }

    @Data
    public static class RideStatusResponse {
        private Long rideId;
        private RideStatus status;
        private SafetyStatus safetyStatus;
        private Boolean passengerLocationUnlocked;
        private DriverProfile driver;
    }

    @Data
    public static class DriverLocationRequest {
        @NotNull
        private Double latitude;
        @NotNull
        private Double longitude;
    }

    @Data
    public static class TrackingResponse {
        private Long rideId;
        private RideStatus status;
        private SafetyStatus safetyStatus;
        private Boolean passengerLocationUnlocked;
        private DriverProfile driver;
        private Double pickupLat;
        private Double pickupLng;
        private Double destinationLat;
        private Double destinationLng;
        private List<DriverLocationView> locations;
    }

    @Data
    public static class DriverProfile {
        private Long driverId;
        private String fullName;
        private String photoUrl;
        private String plateNumber;
        private String vehicleModel;
        private String vehicleColor;
        private BigDecimal rating;
    }

    @Data
    public static class DriverLocationView {
        private Double latitude;
        private Double longitude;
        private Double distanceToPickupMeters;
        private Instant timestamp;
    }
}
