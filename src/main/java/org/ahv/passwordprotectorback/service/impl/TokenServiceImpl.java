package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Token;
import org.ahv.passwordprotectorback.repository.TokenRepository;
import org.ahv.passwordprotectorback.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    @Override
    public List<Token> findAllByUserID(String userID) {
        return tokenRepository.findAllByUserID(userID);
    }

    @Override
    public Token findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void save(Token token) {
        tokenRepository.save(token);
    }

    @Override
    public void delete(Token token) {
        token.setAlreadyUsed(true);
        tokenRepository.save(token);
    }
}
