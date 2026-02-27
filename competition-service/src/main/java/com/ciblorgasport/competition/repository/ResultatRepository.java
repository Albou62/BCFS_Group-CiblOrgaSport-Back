package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Resultat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultatRepository extends JpaRepository<Resultat, Long> {

    List<Resultat> findByMancheId(Long mancheId);

    Optional<Resultat> findByMancheIdAndAthleteId(Long mancheId, Long athleteId);
}
