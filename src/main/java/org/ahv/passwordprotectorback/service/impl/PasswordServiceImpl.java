package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Password;
import org.ahv.passwordprotectorback.repository.PasswordRepository;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final PasswordRepository passwordRepository;

    @Override
    public Password findByElementAndIdentifier(String elementId, String identifier) {
        return passwordRepository.findByElementIDAndIdentifier(elementId, identifier);
    }

    @Override
    public Password findObjectByID(String id) {
        return passwordRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Password password) {
        passwordRepository.save(password);
    }
}
