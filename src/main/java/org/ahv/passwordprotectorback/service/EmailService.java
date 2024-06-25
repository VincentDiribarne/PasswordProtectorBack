package org.ahv.passwordprotectorback.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String token);

    void sendSharePasswordEmail(String to, String username);
}
