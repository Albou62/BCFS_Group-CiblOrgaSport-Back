package com.ciblorgasport.competition.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ciblorgasport.competition.entity.Competition;

public @Entity
@Table(name = "competition_registrations")
public class CompetitionRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "athlete_id")
    private Athlete athlete;

    private LocalDateTime registeredAt;
    private RegistrationStatus status; // PENDING, CONFIRMED, WITHDRAWN

    private Integer bibNumber; // assigned at confirmation time
} {
    
}
