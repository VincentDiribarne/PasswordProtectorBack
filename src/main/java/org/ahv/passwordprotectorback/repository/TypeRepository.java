package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.Type;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TypeRepository extends MongoRepository<Type, String> {
    Type findByName(String name);

    List<Type> findAllByNameContainingIgnoreCase(String name);

    List<Type> findAllByUserID(String userID);
}
