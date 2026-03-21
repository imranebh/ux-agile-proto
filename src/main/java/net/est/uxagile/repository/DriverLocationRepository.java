package net.est.uxagile.repository;

import net.est.uxagile.domain.model.DriverLocation;
import net.est.uxagile.domain.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
    List<DriverLocation> findByRideOrderByTimestampAsc(Ride ride);
}
