package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.UserRole;
import net.est.uxagile.domain.enums.VerificationStatus;
import net.est.uxagile.domain.model.User;
import net.est.uxagile.dto.AuthDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.integration.GoogleAuthProvider;
import net.est.uxagile.mapper.UserMapper;
import net.est.uxagile.repository.UserRepository;
import net.est.uxagile.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final GoogleAuthProvider googleAuthProvider;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        });

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.PASSENGER);
        user.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthDtos.AuthResponse googleLogin(AuthDtos.GoogleLoginRequest request) {
        var info = googleAuthProvider.verify(request.getIdToken());
        User user = userRepository.findByEmail(info.email()).orElseGet(() -> {
            User created = new User();
            created.setEmail(info.email());
            created.setPasswordHash(passwordEncoder.encode("google-login"));
            created.setFullName(info.fullName());
            created.setRole(UserRole.PASSENGER);
            created.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
            created.setGoogleId(info.googleId());
            return userRepository.save(created);
        });
        return buildAuthResponse(user);
    }

    public AuthDtos.UserSummary me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.toSummary(user);
    }

    private AuthDtos.AuthResponse buildAuthResponse(User user) {
        var response = new AuthDtos.AuthResponse();
        response.setToken(jwtService.generateToken(org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build()));
        response.setUser(userMapper.toSummary(user));
        return response;
    }
}
