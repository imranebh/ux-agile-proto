package net.est.uxagile.integration;

public interface IdentityVerificationProvider {
    boolean verify(String fullName, String cniNumber);
}
