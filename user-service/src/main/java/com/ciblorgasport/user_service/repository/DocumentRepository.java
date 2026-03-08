package com.ciblorgasport.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ciblorgasport.user_service.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUploaderUsername(String username);
    List<Document> findByStatus(String status);
}
