package net.est.uxagile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.UserDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserDtos.MeResponse me() {
        return userService.getMe(SecurityUtils.currentUserEmail());
    }

    @PostMapping("/me/verify-cni")
    public UserDtos.MeResponse verify(@Valid @RequestBody UserDtos.VerifyCniRequest request) {
        return userService.verifyCni(SecurityUtils.currentUserEmail(), request);
    }

    @PostMapping("/me/emergency-contact")
    public UserDtos.MeResponse emergency(@Valid @RequestBody UserDtos.EmergencyContactRequest request) {
        return userService.updateEmergencyContact(SecurityUtils.currentUserEmail(), request);
    }
}
