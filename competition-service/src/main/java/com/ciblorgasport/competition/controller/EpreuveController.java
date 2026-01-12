package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.CreateEpreuveRequest;
import com.ciblorgasport.competition.dto.EpreuveDto;
import com.ciblorgasport.competition.service.EpreuveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions/{competitionId}/epreuves")
public class EpreuveController {

    private final EpreuveService service;

    public EpreuveController(EpreuveService service) {
        this.service = service;
    }

    @GetMapping
    public List<EpreuveDto> list(@PathVariable Long competitionId) {
        return service.listByCompetition(competitionId);
    }

    @PostMapping
    public EpreuveDto create(@PathVariable Long competitionId,
                             @RequestBody CreateEpreuveRequest req) {
        return service.create(competitionId, req);
    }
}
