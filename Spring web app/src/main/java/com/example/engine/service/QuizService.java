package com.example.engine.service;

import com.example.engine.entity.Answer;
import com.example.engine.entity.Completed;
import com.example.engine.entity.Quiz;
import com.example.engine.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuizService {
    Quiz getQuizById(Long id);
    void saveQuiz(Quiz quiz, User user);
    void deleteQuizById(Long id, String userName);
    Page<Quiz> getAllAsPage(Pageable pageable) ;
    Page<Completed> getAllCompleted(User user, Pageable pageable);
    boolean solvingQuiz(Quiz quiz,  Answer answer, User user);
}
