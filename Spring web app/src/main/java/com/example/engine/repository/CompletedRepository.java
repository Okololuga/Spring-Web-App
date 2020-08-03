package com.example.engine.repository;

import com.example.engine.entity.Completed;
import com.example.engine.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CompletedRepository extends PagingAndSortingRepository<Completed, Long> {
    Page<Completed> findAllByUser (User user, Pageable pageable);
}
