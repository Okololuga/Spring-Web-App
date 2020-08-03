package com.example.engine.service;

import com.example.engine.entity.Answer;
import com.example.engine.entity.Completed;
import com.example.engine.entity.Quiz;
import com.example.engine.entity.User;
import com.example.engine.exceptions.NotPermittedException;
import com.example.engine.exceptions.QuizNotFoundException;
import com.example.engine.exceptions.UserNotFoundException;
import com.example.engine.repository.CompletedRepository;
import com.example.engine.repository.QuizRepository;
import com.example.engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompletedRepository completedRepository;


    @Override
    public boolean solvingQuiz(Quiz quiz, Answer answer, User user) {
        System.out.println("Правильный ответ " + Arrays.toString(quiz.getAnswer()));
        System.out.println("Мой ответ "+ Arrays.toString(answer.getAnswer()));
        boolean correct;
        User userFromDb = userRepository.findByEmail(user.getEmail())
                .orElseThrow(()->new UsernameNotFoundException("User not found") );
        if (quiz.getAnswer() == null && answer.getAnswer().length == 0) {
            correct = true;
            // проверка на то, что если при добавлении викторины не указали варианты ответов, и при решении отправка пустого ответа будет верным вариантом
        }
        else {
            correct = Arrays.equals(quiz.getAnswer(), answer.getAnswer());
        }
        if (correct) {
            LocalDateTime completedAt = LocalDateTime.now();
            Completed completed = new Completed(user, quiz, completedAt);
            completedRepository.save(completed);
        }
        return correct;
    }


    @Override
    public void saveQuiz(Quiz quiz, User user) {
        Optional<User> userFromDb = userRepository.findByEmail(user.getEmail());
        if (userFromDb.isPresent()) {
            quiz.setUser(user);
            quizRepository.save(quiz);
            return;
        }
        quiz.setUser(null);
        quizRepository.save(quiz);
    }


    @Override
    public void deleteQuizById(Long id, String userName) {
        var quiz = getQuizById(id);
        var userFromDb = userRepository.findByEmail(userName)
                .orElseThrow(UserNotFoundException::new);
        if (Objects.equals(quiz.getUser().getId(), userFromDb.getId())) {
            quizRepository.deleteById(id);
        }
        else{
            throw new NotPermittedException();
        }
    }


    @Override
    public Quiz getQuizById(Long id) {
        var quiz = quizRepository.findById(id);
        return quiz
                .orElseThrow(QuizNotFoundException::new);
    }

    @Override
    public Page<Quiz> getAllAsPage(Pageable pageable) {
        return quizRepository.findAll(pageable);
    }

    @Override
    public Page<Completed> getAllCompleted(User user, Pageable pageable) {
        return completedRepository.findAllByUser(user, pageable);
    }
}