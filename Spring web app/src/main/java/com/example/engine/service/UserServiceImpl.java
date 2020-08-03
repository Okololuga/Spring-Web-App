package com.example.engine.service;

import com.example.engine.entity.User;
import com.example.engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByEmail(s)
                .orElseThrow(()-> new UsernameNotFoundException("User " + s + " not found"));
    }


    public User registerNewUser(String email, String password) {
        String encodedPassword = encoder.encode(password); //шифр пароля
        Optional<User> userFromDb = userRepository.findByEmail(email);
        if (!userFromDb.isPresent()) {       //если такого юзера нет
            User user = new User(email, encodedPassword); //создаем нового
            userRepository.save(user);
            return user;
        }
        return null;
    }
}

