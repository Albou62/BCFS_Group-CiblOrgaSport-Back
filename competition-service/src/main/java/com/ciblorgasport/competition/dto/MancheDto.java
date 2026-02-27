package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.TypeClassement;

public record MancheDto(
        Long id,
        String name,
        Long epreuveId,
        TypeClassement typeClassement,
        Integer ordre
) {
}
