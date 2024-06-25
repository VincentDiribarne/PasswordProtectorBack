package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);

    User findByEmail(String email);
}
