package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.CompetitionDto;
import com.ciblorgasport.competition.dto.CreateCompetitionRequest;
import com.ciblorgasport.competition.service.CompetitionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {

    private final CompetitionService service;

    public CompetitionController(CompetitionService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompetitionDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public CompetitionDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public CompetitionDto create(@RequestBody CreateCompetitionRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CompetitionDto update(@PathVariable Long id,
                                 @RequestBody CreateCompetitionRequest req) {
        return service.update(id, req);
    }

    @PostMapping("/{id}/finish")
    public CompetitionDto finish(@PathVariable Long id) {
        return service.finish(id);
    }
}
