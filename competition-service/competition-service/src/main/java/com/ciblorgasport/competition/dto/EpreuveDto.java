package com.ciblorgasport.competition.dto;

import java.time.LocalDateTime;

public record EpreuveDto(
        Long id,
        String name,
        LocalDateTime horaireAthletes,
        LocalDateTime horairePublic,
        Long competitionId
) { }
