package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.Password;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordRepository extends MongoRepository<Password, String> {
    Password findByElementIDAndIdentifier(String elementId, String identifier);
}
