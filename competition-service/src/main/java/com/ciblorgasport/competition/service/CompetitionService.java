package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.CompetitionDto;
import com.ciblorgasport.competition.dto.CreateCompetitionRequest;
import com.ciblorgasport.competition.entity.Competition;
import com.ciblorgasport.competition.entity.Discipline;
import com.ciblorgasport.competition.repository.CompetitionRepository;
import com.ciblorgasport.competition.repository.DisciplineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepo;
    private final DisciplineRepository disciplineRepo;

    public CompetitionService(CompetitionRepository competitionRepo,
                              DisciplineRepository disciplineRepo) {
        this.competitionRepo = competitionRepo;
        this.disciplineRepo = disciplineRepo;
    }

    public List<CompetitionDto> list() {
        return competitionRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public Competition getEntity(Long id) {
        return competitionRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Competition not found"));
    }

    public CompetitionDto get(Long id) {
        return toDto(getEntity(id));
    }

    public CompetitionDto create(CreateCompetitionRequest req) {
        Competition c = new Competition();
        c.setName(req.name());
        c.setDateDebut(req.dateDebut());
        c.setDateFin(req.dateFin());
        c.setFinished(false);

        if (req.disciplineId() != null) {
            Discipline d = disciplineRepo.findById(req.disciplineId())
                    .orElseThrow(() -> new NoSuchElementException("Discipline not found"));
            c.setDiscipline(d);
        }

        if (!c.isValid()) {
            throw new IllegalArgumentException("Compétition invalide");
        }

        return toDto(competitionRepo.save(c));
    }

    public CompetitionDto update(Long id, CreateCompetitionRequest req) {
        Competition c = getEntity(id);
        c.setName(req.name());
        c.setDateDebut(req.dateDebut());
        c.setDateFin(req.dateFin());

        if (req.disciplineId() != null) {
            Discipline d = disciplineRepo.findById(req.disciplineId())
                    .orElseThrow(() -> new NoSuchElementException("Discipline not found"));
            c.setDiscipline(d);
        } else {
            c.setDiscipline(null);
        }

        if (!c.isValid()) {
            throw new IllegalArgumentException("Compétition invalide");
        }

        return toDto(competitionRepo.save(c));
    }

    public CompetitionDto finish(Long id) {
        Competition c = getEntity(id);
        c.finir();
        return toDto(competitionRepo.save(c));
    }

    private CompetitionDto toDto(Competition c) {
        Long disciplineId = c.getDiscipline() != null ? c.getDiscipline().getId() : null;
        return new CompetitionDto(
                c.getId(),
                c.getName(),
                c.getDateDebut(),
                c.getDateFin(),
                c.isFinished(),
                disciplineId
        );
    }
}
