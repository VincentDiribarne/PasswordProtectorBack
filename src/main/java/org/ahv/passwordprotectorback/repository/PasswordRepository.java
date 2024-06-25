package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.Password;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PasswordRepository extends MongoRepository<Password, String> {
    List<Password> findAllByElementID(String elementId);

    Password findByElementIDAndIdentifier(String elementId, String identifier);
}
