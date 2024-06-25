package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Récupération de mot de passe");
        message.setText("Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant (celui-ci expire dans 60 minutes) : "
                + "http://localhost:5173/reset-password?token=" + token);
        mailSender.send(message);
    }

    @Override
    public void sendSharePasswordEmail(String to, String userId) {
        // Hash userID

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Partage de mot de passe");
        message.setText("Un mot de passe vous a été partagé."
        + "Pour le voir, cliquez sur le lien suivant : " + "http://localhost:5173/shared-password?user=");
        mailSender.send(message);
    }
}
