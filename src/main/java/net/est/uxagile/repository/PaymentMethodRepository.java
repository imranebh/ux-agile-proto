package net.est.uxagile.repository;

import net.est.uxagile.domain.model.PaymentMethod;
import net.est.uxagile.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByUser(User user);
    Optional<PaymentMethod> findByUserAndDefaultMethodTrue(User user);
}
