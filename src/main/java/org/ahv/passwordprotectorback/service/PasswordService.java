package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Password;

public interface PasswordService extends GlobalService<Password> {
    Password findByElementAndIdentifier(String elementId, String identifier);
}
