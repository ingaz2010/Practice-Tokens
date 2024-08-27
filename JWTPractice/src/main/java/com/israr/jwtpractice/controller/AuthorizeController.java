package com.israr.jwtpractice.controller;

import com.israr.jwtpractice.dto.JwtAuthResponse;
import com.israr.jwtpractice.dto.UserDto;
import com.israr.jwtpractice.models.User;
import com.israr.jwtpractice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthorizeController {

    private UserService userService;
    public AuthorizeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home() {
        return "index1";
    }

    @GetMapping("/register")
    public String registerArtCollector(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
//        return "register-art-collectors";
        return "register-users";
    }

    @PostMapping("/register/save")
    public String saveUser(@Valid @ModelAttribute("user") UserDto user, BindingResult result, Model model) {
        User existing = userService.findUserByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "Account with this email already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
//            return "register-art-collectors";
            return "registration-users";
        }
        userService.saveUser(user);
        return "redirect:/register?success";
    }


    @GetMapping("/nextPage")
    public String listArtCollectors(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "nextPage";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
//
//    @PostMapping("/login")
//    public ResponseEntity<JwtAuthResponse> login(@RequestParam String email, @RequestParam String password) {
//        String token = userService.authenticate(email, password);
//
//        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
//        jwtAuthResponse.setAccessToken(token);
//
//        return new ResponseEntity<JwtAuthResponse>(jwtAuthResponse, HttpStatus.OK);
//    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        String token = userService.authenticate(email, password);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return "redirect:/nextPage";
    }
}
