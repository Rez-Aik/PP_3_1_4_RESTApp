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
    @Transactional
    public void update(Long id, User updatedUser, String newPassword, List<String> roleNames) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Обновляем пароль, если новый
        if (newPassword != null && !newPassword.isEmpty()) {
            updatedUser.setPassword(passwordEncoder.encode(newPassword));
        } else {
            updatedUser.setPassword(existingUser.getPassword());
        }

        // Обновляем роли, если новые
        if (roleNames != null && !roleNames.isEmpty()) {
            updatedUser.setRoles(roleService.getRolesSetByUserName(roleNames));
        } else {
            updatedUser.setRoles(existingUser.getRoles());
        }
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }
}
