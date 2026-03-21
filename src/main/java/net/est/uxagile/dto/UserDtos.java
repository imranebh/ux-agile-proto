package net.est.uxagile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.est.uxagile.domain.enums.UserRole;
import net.est.uxagile.domain.enums.VerificationStatus;

public class UserDtos {
    @Data
    public static class MeResponse {
        private Long id;
        private String email;
        private String fullName;
        private String phone;
        private UserRole role;
        private VerificationStatus verificationStatus;
        private String cniMasked;
        private String emergencyContact;
    }

    @Data
    public static class VerifyCniRequest {
        @NotBlank
        private String cniNumber;
    }

    @Data
    public static class EmergencyContactRequest {
        @NotBlank
        private String emergencyContact;
    }
}
