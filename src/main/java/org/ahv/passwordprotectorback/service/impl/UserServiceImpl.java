package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Token;
import org.ahv.passwordprotectorback.model.TokenType;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.repository.TokenRepository;
import org.ahv.passwordprotectorback.repository.UserRepository;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<String> findAllUsernames() {
        return findAll().stream().map(User::getUsername).toList();
    }

    @Override
    public List<String> findAllEmails() {
        return findAll().stream().map(User::getEmail).toList();
    }


    //Global method
    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream().sorted(Comparator.comparing(User::getUsername)).toList();
    }

    @Override
    public User findObjectByID(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(User object) {
        userRepository.delete(object);
    }

    // ResetPassword
    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        Token myToken = Token.builder()
                .token(token)
                .userID(user.getId())
                .type(TokenType.PASSWORD_RESET_TOKEN)
                .alreadyUsed(false)
                .expirationDate(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .build();

        tokenRepository.save(myToken);
    }

    @Override
    public void changePassword(User user, String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}