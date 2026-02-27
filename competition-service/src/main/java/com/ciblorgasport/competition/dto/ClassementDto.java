package com.ciblorgasport.competition.dto;

import com.ciblorgasport.competition.entity.TypeClassement;

import java.util.List;

public record ClassementDto(
        Long mancheId,
        TypeClassement typeClassement,
        List<ResultatDto> resultats
) {
}
