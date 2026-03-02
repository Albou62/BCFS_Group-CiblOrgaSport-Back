package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.CreateEpreuveRequest;
import com.ciblorgasport.competition.dto.EpreuveDto;
import com.ciblorgasport.competition.entity.Competition;
import com.ciblorgasport.competition.entity.Epreuve;
import com.ciblorgasport.competition.repository.EpreuveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EpreuveService {

    private final EpreuveRepository epreuveRepo;
    private final CompetitionService competitionService;

    public EpreuveService(EpreuveRepository epreuveRepo,
                          CompetitionService competitionService) {
        this.epreuveRepo = epreuveRepo;
        this.competitionService = competitionService;
    }

    public List<EpreuveDto> listByCompetition(Long competitionId) {
        return epreuveRepo.findByCompetitionId(competitionId).stream()
                .map(this::toDto)
                .toList();
    }

    public EpreuveDto create(Long competitionId, CreateEpreuveRequest req) {
        Competition comp = competitionService.getEntity(competitionId);

        Epreuve e = new Epreuve();
        e.setName(req.name());
        e.setHoraireAthletes(req.horaireAthletes());
        e.setHorairePublic(req.horairePublic());
        e.setCompetition(comp);

        return toDto(epreuveRepo.save(e));
    }

    public List<EpreuveDto> listUpcoming(int limit) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        
        List<Epreuve> all = epreuveRepo.findByHorairePublicAfterOrderByHorairePublicAsc(threshold);
        return all.stream()
                .limit(limit)
                .map(this::toDto)
                .toList();
    }

    private EpreuveDto toDto(Epreuve e) {
        return new EpreuveDto(
                e.getId(),
                e.getName(),
                e.getHoraireAthletes(),
                e.getHorairePublic(),
                e.getCompetition() != null ? e.getCompetition().getId() : null,
                e.getCompetition().getName()
        );
    }
}
