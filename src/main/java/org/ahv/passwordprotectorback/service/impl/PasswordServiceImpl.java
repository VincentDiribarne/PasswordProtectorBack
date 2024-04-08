package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Password;
import org.ahv.passwordprotectorback.repository.PasswordRepository;
import org.ahv.passwordprotectorback.service.PasswordService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final PasswordRepository passwordRepository;

    @Override
    public List<Password> findAllByElementId(String elementId) {
        return passwordRepository.findAllByElementID(elementId);
    }

    @Override
    public List<String> findAllIdentifier() {
        return findAll().stream().map(Password::getIdentifier).toList();
    }

    @Override
    public Password findByElementAndIdentifier(String elementId, String identifier) {
        return passwordRepository.findByElementIDAndIdentifier(elementId, identifier);
    }

    //Global method
    @Override
    public List<Password> findAll() {
        return passwordRepository.findAll().stream().sorted(Comparator.comparing(Password::getIdentifier)).toList();
    }

    @Override
    public Password findObjectByID(String id) {
        return passwordRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Password password) {
        passwordRepository.save(password);
    }

    @Override
    public void delete(Password object) {
        passwordRepository.delete(object);
    }
}
