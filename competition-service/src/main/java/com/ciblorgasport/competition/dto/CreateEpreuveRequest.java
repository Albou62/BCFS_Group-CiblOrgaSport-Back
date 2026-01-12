package com.ciblorgasport.competition.dto;

import java.time.LocalDateTime;

public record CreateEpreuveRequest(
        String name,
        LocalDateTime horaireAthletes,
        LocalDateTime horairePublic
) { }
