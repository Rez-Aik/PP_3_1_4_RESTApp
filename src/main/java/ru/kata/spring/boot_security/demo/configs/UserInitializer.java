package ru.kata.spring.boot_security.demo.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserInitializer {

    private final RoleService roleService;
    private final UserService userService;

    public UserInitializer(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @PostConstruct
    public void initialize() {
        try {
            if (userService.getAllUsers().isEmpty()) {
                Role roleAdmin = new Role("ADMIN");
                Role roleUser = new Role("USER");

                roleService.saveRole(roleAdmin);
                roleService.saveRole(roleUser);

                User admin = new User("admin", 12, "admin@admin.com");
                admin.setPassword("admin");
                admin.setRoles(Set.of(roleAdmin));

                User user = new User("test_user", 20, "test@test.com");
                user.setPassword("user");
                user.setRoles(Set.of(roleUser));

                userService.addUser(admin);
                userService.addUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
