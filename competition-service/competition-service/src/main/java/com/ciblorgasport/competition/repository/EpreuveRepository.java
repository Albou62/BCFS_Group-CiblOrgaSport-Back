package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Epreuve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EpreuveRepository extends JpaRepository<Epreuve, Long> {

    List<Epreuve> findByCompetitionId(Long competitionId);

    List<Epreuve> findByHorairePublicAfterOrderByHorairePublicAsc(LocalDateTime dateTime);
}
