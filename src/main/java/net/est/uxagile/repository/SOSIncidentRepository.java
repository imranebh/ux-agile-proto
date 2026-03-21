package net.est.uxagile.repository;

import net.est.uxagile.domain.model.SOSIncident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SOSIncidentRepository extends JpaRepository<SOSIncident, Long> {
}
