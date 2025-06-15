package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.*;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public void addUser(User user, List<String> roleNames) {
        // Проверка пароля
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Установка ролей
        if (roleNames != null && !roleNames.isEmpty()) {
            user.setRoles(roleService.getRolesSetByUserName(roleNames));
        } else {
            Set<Role> defaultRoles = new HashSet<>();
            defaultRoles.add(roleService.findRoleByName(Role.defaultRoleName));
            user.setRoles(defaultRoles);
        }

        // Кодирование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь с Email: " + email + " не найден"));
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public void update(Long id, User user, String password, List<String> roleNames) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Обновление полей
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getAge() != null) {
            existingUser.setAge(user.getAge());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        // Обновление пароля
        if (password != null && !password.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        // Обновление ролей
        if (roleNames != null) {
            existingUser.setRoles(roleService.getRolesSetByUserName(roleNames));
        }

        userRepository.save(existingUser);
    }
}
