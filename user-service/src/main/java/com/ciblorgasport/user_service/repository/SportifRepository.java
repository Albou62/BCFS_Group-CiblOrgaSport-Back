package com.ciblorgasport.user_service.repository;

import com.ciblorgasport.user_service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SportifRepository extends JpaRepository<SportifProfile, Long> {
	SportifProfile findByUsername(String username);
}

