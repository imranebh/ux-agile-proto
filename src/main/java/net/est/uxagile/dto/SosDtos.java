package net.est.uxagile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class SosDtos {
    @Data
    public static class SosRequest {
        @NotNull
        private Long rideId;
        @NotBlank
        private String reason;
    }

    @Data
    public static class SosResponse {
        private Long id;
        private Long rideId;
        private String reason;
    }
}
