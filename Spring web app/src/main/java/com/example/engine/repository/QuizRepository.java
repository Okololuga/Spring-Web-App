package com.example.engine.repository;


import com.example.engine.entity.Quiz;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuizRepository extends PagingAndSortingRepository<Quiz, Long> {
    List<Quiz> findByTitleContaining(String value);
}