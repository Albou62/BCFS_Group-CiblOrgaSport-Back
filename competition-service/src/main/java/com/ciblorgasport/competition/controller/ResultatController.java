package com.ciblorgasport.competition.controller;

import com.ciblorgasport.competition.dto.ClassementDto;
import com.ciblorgasport.competition.dto.CreateResultatRequest;
import com.ciblorgasport.competition.dto.PodiumDto;
import com.ciblorgasport.competition.dto.ResultatDto;
import com.ciblorgasport.competition.dto.UpdateResultatRequest;
import com.ciblorgasport.competition.service.ClassementService;
import com.ciblorgasport.competition.service.ResultatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manches/{mancheId}")
public class ResultatController {

    private final ResultatService resultatService;
    private final ClassementService classementService;

    public ResultatController(ResultatService resultatService,
                              ClassementService classementService) {
        this.resultatService = resultatService;
        this.classementService = classementService;
    }

    @PostMapping("/resultats")
    public ResultatDto create(@PathVariable Long mancheId,
                              @RequestBody CreateResultatRequest request) {
        return resultatService.create(mancheId, request);
    }

    @PutMapping("/resultats/{resultatId}")
    public ResultatDto update(@PathVariable Long mancheId,
                              @PathVariable Long resultatId,
                              @RequestBody UpdateResultatRequest request) {
        return resultatService.update(mancheId, resultatId, request);
    }

    @GetMapping("/resultats")
    public List<ResultatDto> list(@PathVariable Long mancheId) {
        return resultatService.list(mancheId);
    }

    @GetMapping("/classement")
    public ClassementDto classement(@PathVariable Long mancheId) {
        return classementService.getClassement(mancheId);
    }

    @GetMapping("/podium")
    public PodiumDto podium(@PathVariable Long mancheId) {
        return classementService.getPodium(mancheId);
    }
}
