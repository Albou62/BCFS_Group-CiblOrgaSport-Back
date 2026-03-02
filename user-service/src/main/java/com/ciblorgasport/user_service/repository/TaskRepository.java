package com.ciblorgasport.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciblorgasport.user_service.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByVolontaireId(Long volontaireId);
    List<Task> findByVolontaireUsername(String username);
}