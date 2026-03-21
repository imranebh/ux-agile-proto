package net.est.uxagile.repository;

import net.est.uxagile.domain.model.Driver;
import net.est.uxagile.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByUser(User user);
}
