package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Manche;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MancheRepository extends JpaRepository<Manche, Long> {

    List<Manche> findByEpreuveIdOrderByOrdreAsc(Long epreuveId);
}
