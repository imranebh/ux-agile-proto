package net.est.uxagile.integration;

import org.springframework.stereotype.Component;

@Component
public class MockGoogleAuthProvider implements GoogleAuthProvider {
    @Override
    public GoogleUserInfo verify(String idToken) {
        String token = (idToken == null || idToken.isBlank()) ? "demo" : idToken;
        return new GoogleUserInfo(token + "@gmail.com", "Google User " + token, "google-" + token);
    }
}
