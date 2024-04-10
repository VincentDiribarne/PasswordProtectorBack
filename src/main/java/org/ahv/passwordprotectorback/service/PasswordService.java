package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Password;

import java.util.List;

public interface PasswordService extends GlobalService<Password> {
    Password findByElementAndIdentifier(String elementId, String identifier);

    List<Password> findAllByElementId(String elementId);

    List<String> findAllIdentifierByElementID(String elementId);
}
