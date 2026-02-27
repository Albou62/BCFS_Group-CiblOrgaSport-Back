package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.CreateMancheRequest;
import com.ciblorgasport.competition.dto.MancheDto;
import com.ciblorgasport.competition.service.MancheService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/epreuves/{epreuveId}/manches")
public class MancheController {

    private final MancheService mancheService;

    public MancheController(MancheService mancheService) {
        this.mancheService = mancheService;
    }

    @PostMapping
    public MancheDto create(@PathVariable Long epreuveId,
                            @RequestBody CreateMancheRequest request) {
        return mancheService.create(epreuveId, request);
    }

    @GetMapping
    public List<MancheDto> list(@PathVariable Long epreuveId) {
        return mancheService.listByEpreuve(epreuveId);
    }
}
