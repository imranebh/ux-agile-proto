package net.est.uxagile.integration;

import org.springframework.stereotype.Component;

@Component
public class MockIdentityVerificationProvider implements IdentityVerificationProvider {
    @Override
    public boolean verify(String fullName, String cniNumber) {
        return cniNumber != null && cniNumber.length() >= 8 && fullName != null && !fullName.isBlank();
    }
}
