package com.example.engine.controller;


import com.example.engine.entity.*;
import com.example.engine.service.QuizService;
import com.example.engine.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.security.Principal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserServiceImpl userService;

    /*  добавляю на сервак новую викторину   */
    @PostMapping(value = "/api/quizzes", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Quiz quiz,
                                    @Autowired Authentication authentication) {
        String userName = authentication.getName();
        User user = (User) userService.loadUserByUsername(userName);
        quizService.saveQuiz(quiz, user);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    /*     удаление викторины   */
    @DeleteMapping(path = "/api/quizzes/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteQuiz(@PathVariable long id,
                           @Autowired Authentication authentication) {
        quizService.deleteQuizById(id, authentication.getName());
    }


    /*  запрос викторины по id  */
    @GetMapping("/api/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz (@PathVariable long id) {
        final Quiz quiz = quizService.getQuizById(id);
        return quiz != null
                ? new ResponseEntity<>(quiz, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /*  запрос всех викторин со страницами  */
    @GetMapping("/api/quizzes")
    public Page<Quiz> getAllQuizPaging(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return quizService.getAllAsPage(pageable);
    }

    /* запрос всех пройденных, по юзеру*/
    @GetMapping("/api/quizzes/completed")
    public Page<Completed> getAllCompletedPaging( @Autowired Authentication authentication,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "completedAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        User user = (User) authentication.getPrincipal();
        return quizService.getAllCompleted(user, pageable);
    }


    /*  решаю викторину по id   */
    @PostMapping(value = "/api/quizzes/{id}/solve")
    public ResponseEntity<Feedback> solving (@PathVariable(name = "id") long id,
                                             @RequestBody Answer answer,
                                             @Autowired Authentication authentication) {
        String userName = authentication.getName();
        User user = (User) userService.loadUserByUsername(userName);
        Quiz quiz = quizService.getQuizById(id);
        return new ResponseEntity<>(new Feedback(quizService.solvingQuiz(quiz, answer, user)), HttpStatus.OK);
    }
}
