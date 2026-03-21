package net.est.uxagile.repository;

import net.est.uxagile.domain.model.Invoice;
import net.est.uxagile.domain.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByRide(Ride ride);
}
