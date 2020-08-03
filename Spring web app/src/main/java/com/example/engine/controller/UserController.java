package com.example.engine.controller;

import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import com.example.engine.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

//регистрация пользователя
    @PostMapping(value = "/api/register", consumes = "application/json")
    public ResponseEntity<?> create(@Valid @RequestBody User user) {
        User newUser = userService.registerNewUser(user.getEmail(),user.getPassword());
        return newUser == null ? new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST)
                                : new ResponseEntity<>(newUser, HttpStatus.OK);
   }

}
