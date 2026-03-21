package net.est.uxagile.repository;

import net.est.uxagile.domain.model.Payment;
import net.est.uxagile.domain.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRide(Ride ride);
}
