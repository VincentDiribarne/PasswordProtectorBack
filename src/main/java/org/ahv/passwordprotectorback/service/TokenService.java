package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.Token;

import java.util.List;

public interface TokenService {
    List<Token> findAllByUserID(String userID);

    Token findByToken(String token);

    void save(Token token);

    void delete(Token token);
}
