package com.ciblorgasport.competition.dto;

import java.time.LocalDate;

public record CreateCompetitionRequest(
        String name,
        LocalDate dateDebut,
        LocalDate dateFin,
        Long disciplineId
) { }
