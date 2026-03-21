package net.est.uxagile.repository;

import net.est.uxagile.domain.enums.RideStatus;
import net.est.uxagile.domain.model.Ride;
import net.est.uxagile.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByPassenger(User passenger);
    List<Ride> findByStatusIn(List<RideStatus> statuses);
}
