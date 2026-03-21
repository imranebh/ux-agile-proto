package net.est.uxagile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.est.uxagile.dto.AuthDtos;
import net.est.uxagile.security.SecurityUtils;
import net.est.uxagile.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/google")
    public AuthDtos.AuthResponse google(@Valid @RequestBody AuthDtos.GoogleLoginRequest request) {
        return authService.googleLogin(request);
    }

    @GetMapping("/me")
    public AuthDtos.UserSummary me() {
        return authService.me(SecurityUtils.currentUserEmail());
    }
}
