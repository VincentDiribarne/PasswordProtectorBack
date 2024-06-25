package org.ahv.passwordprotectorback.repository;

import org.ahv.passwordprotectorback.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TokenRepository extends MongoRepository<Token, String> {
    List<Token> findAllByUserID(String userID);

    Token findByToken(String token);
}