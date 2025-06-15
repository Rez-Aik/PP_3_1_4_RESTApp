package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {

    List<User> getAllUsers();

    User getUserById(Long id);

    void addUser(User user, List<String> roleNames);

    void deleteUser(Long id);

    Optional<User> findByName(String name);

    void update(Long id, User user, String password, List<String> roleNames);
}

