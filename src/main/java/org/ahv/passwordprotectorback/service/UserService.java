package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.User;

import java.util.List;

public interface UserService extends GlobalService<User> {
    User findByUsername(String username);

    User findByEmail(String email);

    List<String> findAllUsernames();

    List<String> findAllEmails();

    // Reset password
    void createPasswordResetTokenForUser(User user, String token);

    void changePassword(User user, String password);

}
