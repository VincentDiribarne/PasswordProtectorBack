package org.ahv.passwordprotectorback.service;

import org.ahv.passwordprotectorback.model.User;

public interface UserService extends GlobalService<User> {
    User findByUsername(String username);

    User findByEmail(String email);
}
