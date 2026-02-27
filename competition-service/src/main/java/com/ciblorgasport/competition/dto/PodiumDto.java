package com.ciblorgasport.competition.dto;

public record PodiumDto(
        Long mancheId,
        ResultatDto orResultat,
        ResultatDto argentResultat,
        ResultatDto bronzeResultat
) {
}
