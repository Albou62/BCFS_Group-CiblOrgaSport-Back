package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.StatutResultat;

import java.math.BigDecimal;
import java.time.LocalTime;

public record CreateResultatRequest(
        Long athleteId,
        BigDecimal score,
        LocalTime temps,
        StatutResultat statut
) {
}
