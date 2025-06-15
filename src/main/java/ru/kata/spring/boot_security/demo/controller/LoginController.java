package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {return "login"; // Имя шаблона без .html
    }
    @GetMapping("/admin")
    public String adminPage(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("currentUser", currentUser);
        return "admin"; // имя вашего HTML-файла (admin.html)
    }

    @GetMapping("/user")
    public String userPage(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        model.addAttribute("currentUser", currentUser);
        return "user"; // имя вашего HTML-файла (admin.html)
    }

}
