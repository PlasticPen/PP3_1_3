package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;

@Controller
@RequestMapping("/user")
public class UsersController {

    @GetMapping
    public String userHomePage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "homePage";
    }

}
