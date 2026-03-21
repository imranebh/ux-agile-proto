package net.est.uxagile.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.domain.enums.SafetyStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private User passenger;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private String destinationAddress;

    @Column(nullable = false)
    private Double pickupLat;

    @Column(nullable = false)
    private Double pickupLng;

    @Column(nullable = false)
    private Double destinationLat;

    @Column(nullable = false)
    private Double destinationLng;

    @Column(nullable = false)
    private Boolean passengerLocationUnlocked = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status = RideStatus.REQUESTED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SafetyStatus safetyStatus = SafetyStatus.NORMAL;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private Instant requestedAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    private Instant completedAt;
}
