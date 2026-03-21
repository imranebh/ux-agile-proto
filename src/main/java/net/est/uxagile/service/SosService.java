package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.SafetyStatus;
import net.est.uxagile.domain.model.SOSIncident;
import net.est.uxagile.dto.SosDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.repository.RideRepository;
import net.est.uxagile.repository.SOSIncidentRepository;
import net.est.uxagile.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SosService {

    private final SOSIncidentRepository sosIncidentRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    @Transactional
    public SosDtos.SosResponse create(String email, SosDtos.SosRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        var ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Ride not found"));
        if (!ride.getPassenger().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your ride");
        }

        SOSIncident incident = new SOSIncident();
        incident.setRide(ride);
        incident.setUser(user);
        incident.setReason(request.getReason());
        incident = sosIncidentRepository.save(incident);
        ride.setSafetyStatus(SafetyStatus.SOS_TRIGGERED);
        rideRepository.save(ride);

        SosDtos.SosResponse response = new SosDtos.SosResponse();
        response.setId(incident.getId());
        response.setRideId(ride.getId());
        response.setReason(incident.getReason());
        return response;
    }
}
