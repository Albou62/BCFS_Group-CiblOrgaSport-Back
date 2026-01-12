package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
}
