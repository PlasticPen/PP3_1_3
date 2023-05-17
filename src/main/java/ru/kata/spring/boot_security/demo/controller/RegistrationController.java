package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class RegistrationController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String initPage(@AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/createAdmin";
        }
        else if (userService.findUserById(user.getId()).getId() == 0) {
            return "redirect:/login";
        }
        return "redirect:/user";
    }

    @GetMapping("/createAdmin")
    public String createAdmin() {
        userService.createAdminUser();
        return "redirect:/login";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute("user") User user) {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") User userForm, Model model) {
        if (!userService.saveUser(userForm)){
            model.addAttribute("usernameError", true);
            return "registration";
        }
        return "redirect:/login";
    }
}
