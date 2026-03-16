package com.ciblorgasport.competition.repository;

import com.ciblorgasport.competition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRegistrationRepository 
    extends JpaRepository<CompetitionRegistration, UUID> {

    List<CompetitionRegistration> findByCompetitionId(UUID competitionId);
    List<CompetitionRegistration> findByAthleteId(UUID athleteId);
    Optional<CompetitionRegistration> findByCompetitionIdAndAthleteId(
        UUID competitionId, UUID athleteId);
    long countByCompetitionIdAndStatus(UUID competitionId, RegistrationStatus status);
}