package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.StatutResultat;

import java.math.BigDecimal;
import java.time.LocalTime;

public record UpdateResultatRequest(
        BigDecimal score,
        LocalTime temps,
        StatutResultat statut
) {
}
