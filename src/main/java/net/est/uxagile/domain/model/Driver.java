package net.est.uxagile.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import net.est.uxagile.domain.enums.DriverStatus;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status = DriverStatus.AVAILABLE;

    private String photoUrl;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    private Double currentLat;
    private Double currentLng;
}
