package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/current")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        return ResponseEntity.ok((User) userService.loadUserByUsername(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody Map<String, Object> requestData, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        User user = new User();
        user.setName((String) requestData.get("name"));
        user.setEmail((String) requestData.get("email"));
        user.setAge(Integer.parseInt(requestData.get("age").toString()));
        user.setPassword((String) requestData.get("password"));

        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) requestData.get("roleNames");

        userService.addUser(user, roleNames);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<User> getUserForEdit(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @Valid @RequestBody Map<String, Object> requestData,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> userMap = (Map<String, Object>) requestData.get("updatedUser");
        String newPassword = (String) requestData.get("newPassword");
        @SuppressWarnings("unchecked")
        List<String> roleNames = (List<String>) requestData.get("roleNames");

        User updatedUser = new User();
        updatedUser.setName((String) userMap.get("name"));
        updatedUser.setEmail((String) userMap.get("email"));
        updatedUser.setAge(userMap.get("age") != null ? Integer.parseInt(userMap.get("age").toString()) : null);

        userService.update(id, updatedUser, newPassword, roleNames);

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
