package net.est.uxagile.service;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.VerificationStatus;
import net.est.uxagile.domain.model.User;
import net.est.uxagile.dto.UserDtos;
import net.est.uxagile.exception.ApiException;
import net.est.uxagile.integration.IdentityVerificationProvider;
import net.est.uxagile.mapper.UserMapper;
import net.est.uxagile.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final IdentityVerificationProvider identityVerificationProvider;
    private final UserMapper userMapper;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserDtos.MeResponse getMe(String email) {
        return userMapper.toMeResponse(findByEmail(email));
    }

    @Transactional
    public UserDtos.MeResponse verifyCni(String email, UserDtos.VerifyCniRequest request) {
        User user = findByEmail(email);
        boolean valid = identityVerificationProvider.verify(user.getFullName(), request.getCniNumber());
        if (!valid) {
            user.setVerificationStatus(VerificationStatus.REJECTED);
            throw new ApiException(HttpStatus.BAD_REQUEST, "CNI verification failed");
        }
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        user.setCniMasked(maskCni(request.getCniNumber()));
        userRepository.save(user);
        return userMapper.toMeResponse(user);
    }

    @Transactional
    public UserDtos.MeResponse updateEmergencyContact(String email, UserDtos.EmergencyContactRequest request) {
        User user = findByEmail(email);
        user.setEmergencyContact(request.getEmergencyContact());
        userRepository.save(user);
        return userMapper.toMeResponse(user);
    }

    private String maskCni(String cniNumber) {
        int visible = Math.min(2, cniNumber.length());
        String suffix = cniNumber.substring(cniNumber.length() - visible);
        return "*".repeat(Math.max(0, cniNumber.length() - visible)) + suffix;
    }
}
