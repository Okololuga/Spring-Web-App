package com.example.engine.controller;

import com.example.engine.entity.Answer;
import com.example.engine.entity.Quiz;
import com.example.engine.entity.User;
import com.example.engine.exceptions.NotPermittedException;
import com.example.engine.exceptions.QuizNotFoundException;
import com.example.engine.repository.UserRepository;
import com.example.engine.service.QuizServiceImpl;
import com.example.engine.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizServiceImpl quizService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private Authentication authentication;

    @MockBean
    private UserRepository userRepository;

    private Quiz quiz = new Quiz("The Java Logo", "What is depicted on the Java logo?", new String[] {"Robot","Tea leaf","Cup of coffee","Bug"}, new int[] {2});

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void createQuiz_whenSuccessfully() throws Exception {

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quiz)))
                .andExpect(jsonPath("$.id").value(quiz.getId()))
                .andExpect(jsonPath("$.title").value(quiz.getTitle()))
                .andExpect(jsonPath("$.text").value(quiz.getText()))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options", hasItem("Robot")))
                .andExpect(jsonPath("$.options", hasItem("Tea leaf")))
                .andExpect(jsonPath("$.options", hasItem("Cup of coffee")))
                .andExpect(jsonPath("$.options", hasItem("Bug")))
                .andExpect(status().isOk());
    }

    @Test
    public void createQuiz_whenUnauthorized() throws Exception {

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quiz)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void createQuiz_whenNoAnswer() throws Exception {
        quiz.setAnswer(null);

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quiz)))
                .andExpect(jsonPath("$.id").value(quiz.getId()))
                .andExpect(jsonPath("$.title").value(quiz.getTitle()))
                .andExpect(jsonPath("$.text").value(quiz.getText()))
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options", hasSize(4)))
                .andExpect(jsonPath("$.options", hasItem("Robot")))
                .andExpect(jsonPath("$.options", hasItem("Tea leaf")))
                .andExpect(jsonPath("$.options", hasItem("Cup of coffee")))
                .andExpect(jsonPath("$.options", hasItem("Bug")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void createQuiz_whenNoOptions() throws Exception {
        quiz.setOptions(null);

        mockMvc.perform(post("/api/quizzes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(quiz)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void getQuiz_whenExists() throws Exception {

        when(quizService.getQuizById(anyLong())).thenReturn(quiz);

        mockMvc.perform(get((String.format("/api/quizzes/%s", quiz.getId()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").doesNotExist());
    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void getQuiz_whenDoesNotExists() throws Exception {

        when(quizService.getQuizById(anyLong())).thenThrow(QuizNotFoundException.class);

        mockMvc.perform(get((String.format("/api/quizzes/%d", 123))))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getQuiz_whenUnauthorized() throws Exception {

        mockMvc.perform(get((String.format("/api/quizzes/%d", 123))))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void getAllQuizList_whenNoQuizzes() throws Exception {
        when(quizService.getAllAsPage(any())).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }


    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void solveQuiz_whenCorrectAnswer() throws Exception {
        when(quizService.solvingQuiz(any(), any(), any())).thenReturn(true);
        Answer answer = new Answer(new int[]{1});

        mockMvc.perform(post(String.format("/api/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void solveQuiz_whenIncorrectAnswer() throws Exception {
        when(quizService.solvingQuiz(any(), any(), any())).thenReturn(false);
        Answer answer = new Answer(new int[]{1});

        mockMvc.perform(post(String.format("/api/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void solveQuiz_whenUnauthorized() throws Exception {
        Answer answer = new Answer(new int[]{1});
        mockMvc.perform(post(String.format("/api/quizzes/%d/solve", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answer)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@q.com", password = "12345")
    public void deleteQuiz_whenSuccessful() throws Exception {
        mockMvc.perform(delete(String.format("/api/quizzes/%d", 1)))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void deleteQuiz_whenUnauthorized() throws Exception {
        mockMvc.perform(delete(String.format("/api/quizzes/%d", 1)))
                .andExpect(status().isUnauthorized());
    }
}