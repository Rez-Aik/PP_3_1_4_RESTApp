package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String getUsers(Model model, Principal principal) {
        UserDetails currentUser = userService.loadUserByUsername(principal.getName()); // Для admin/users
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("newUser", new User()); // Для формы добавления
        model.addAttribute("activePage", "admin"); // Для подсветки текущей страницы
        model.addAttribute("currentUser", currentUser); // Для admin/users
        model.addAttribute("userToEdit", new User()); // Добавляем пустого пользователя для формы редактирования
        return "admin/users";
    }


    @PostMapping
    public String addUser(@ModelAttribute("newUser") @Valid User user,
                          BindingResult bindingResult,
                          @RequestParam("roleNames") List<String> roleNames,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.getAllUsers());
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "redirect:/admin";
        }
        user.setRoles(roleService.getRolesSetByUserName(roleNames));
        userService.addUser(user);
        return "redirect:/admin";
    }
    @GetMapping("/get-user/{id}")
    @ResponseBody
    public User getUserForEdit(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "password", required = false) String password,
                             @RequestParam(value = "roleNames", required = false) List<String> roleNames,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "admin/users";
        }
        userService.update(id, user, password, roleNames);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @GetMapping("/user/{id}")
    public String findUserById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("activePage", "admin");
        return "user/user";
    }
}
