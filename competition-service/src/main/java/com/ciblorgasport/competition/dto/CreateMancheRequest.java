package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.TypeClassement;

public record CreateMancheRequest(
        String name,
        TypeClassement typeClassement,
        Integer ordre
) {
}
