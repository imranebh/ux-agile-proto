package net.est.uxagile.mapper;

import net.est.uxagile.domain.model.User;
import net.est.uxagile.dto.AuthDtos;
import net.est.uxagile.dto.UserDtos;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public AuthDtos.UserSummary toSummary(User user) {
        AuthDtos.UserSummary summary = new AuthDtos.UserSummary();
        summary.setId(user.getId());
        summary.setEmail(user.getEmail());
        summary.setFullName(user.getFullName());
        summary.setRole(user.getRole());
        summary.setVerificationStatus(user.getVerificationStatus());
        return summary;
    }

    public UserDtos.MeResponse toMeResponse(User user) {
        UserDtos.MeResponse response = new UserDtos.MeResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());
        response.setVerificationStatus(user.getVerificationStatus());
        response.setCniMasked(user.getCniMasked());
        response.setEmergencyContact(user.getEmergencyContact());
        return response;
    }
}
