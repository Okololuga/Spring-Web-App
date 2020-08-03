package com.example.engine.controller;

import com.example.engine.entity.User;
import com.example.engine.exceptions.DuplicateEmailException;
import com.example.engine.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testRegister_whenSuccessful() throws Exception {
        User user = new User();
        user.setEmail("test@q.com");
        user.setPassword("12345");

        when(userService.registerNewUser(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is2xxSuccessful());
      }

    @Test
    public void testRegister_whenPasswordTooShort() throws Exception {
        User user = new User();
        user.setEmail("test@q.com");
        user.setPassword("123");

        when(userService.registerNewUser(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testRegister_whenUsernameIsNotEmail() throws Exception {
        User user = new User();
        user.setEmail("abcde");
        user.setPassword("12345");

        when(userService.registerNewUser(anyString(), anyString())).thenReturn(user);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegister_whenDuplicateEmail() throws Exception {
        User user = new User();
        user.setEmail("test@q.com");
        user.setPassword("12345");

        when(userService.registerNewUser(anyString(), anyString()))
                .thenThrow(DuplicateEmailException.class);

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }




}