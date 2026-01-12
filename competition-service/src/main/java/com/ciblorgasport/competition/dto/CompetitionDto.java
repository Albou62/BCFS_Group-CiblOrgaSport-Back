package com.ciblorgasport.competition.dto;

import java.time.LocalDate;

public record CompetitionDto(
        Long id,
        String name,
        LocalDate dateDebut,
        LocalDate dateFin,
        boolean finished,
        Long disciplineId
) { }
