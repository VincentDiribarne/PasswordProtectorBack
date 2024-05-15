package org.ahv.passwordprotectorback.security;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        org.ahv.passwordprotectorback.model.User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .build();
    }
}