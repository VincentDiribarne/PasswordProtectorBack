package org.ahv.passwordprotectorback.service.impl;

import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.repository.UserRepository;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
}