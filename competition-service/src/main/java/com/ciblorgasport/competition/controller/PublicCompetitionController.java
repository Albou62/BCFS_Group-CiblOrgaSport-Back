package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.EpreuveDto;
import com.ciblorgasport.competition.service.EpreuveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicCompetitionController {

    private final EpreuveService epreuveService;

    public PublicCompetitionController(EpreuveService epreuveService) {
        this.epreuveService = epreuveService;
    }

    @GetMapping("/upcoming-epreuves")
    public List<EpreuveDto> upcoming(@RequestParam(defaultValue = "3") int limit) {
        return epreuveService.listUpcoming(limit);
    }
    
    @GetMapping("/epreuves")
    public List<EpreuveDto> getAllEpreuves() {
        return epreuveService.findAll(); 
    }
}
