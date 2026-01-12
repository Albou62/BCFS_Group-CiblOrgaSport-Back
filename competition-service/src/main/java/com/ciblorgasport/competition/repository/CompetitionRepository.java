package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
