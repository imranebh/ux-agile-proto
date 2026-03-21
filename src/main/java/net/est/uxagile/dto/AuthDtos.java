package net.est.uxagile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.est.uxagile.domain.enums.UserRole;
import net.est.uxagile.domain.enums.VerificationStatus;

public class AuthDtos {
    @Data
    public static class RegisterRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String fullName;
        @NotBlank
        private String phone;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class GoogleLoginRequest {
        @NotBlank
        private String idToken;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private UserSummary user;
    }

    @Data
    public static class UserSummary {
        private Long id;
        private String email;
        private String fullName;
        private UserRole role;
        private VerificationStatus verificationStatus;
    }
}
