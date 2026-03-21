package net.est.uxagile.integration;

public interface GoogleAuthProvider {
    GoogleUserInfo verify(String idToken);

    record GoogleUserInfo(String email, String fullName, String googleId) {
    }
}
