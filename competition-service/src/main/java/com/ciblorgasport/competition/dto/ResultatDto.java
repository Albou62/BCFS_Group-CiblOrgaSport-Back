package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.StatutResultat;
import com.ciblorgasport.competition.entity.TypeMedaille;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ResultatDto(
        Long id,
        Long athleteId,
        Long mancheId,
        BigDecimal score,
        LocalTime temps,
        Integer rang,
        StatutResultat statut,
        TypeMedaille medaille
) {
}
