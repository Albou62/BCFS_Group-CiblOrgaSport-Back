package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.CreateResultatRequest;
import com.ciblorgasport.competition.dto.ResultatDto;
import com.ciblorgasport.competition.dto.UpdateResultatRequest;
import com.ciblorgasport.competition.entity.Manche;
import com.ciblorgasport.competition.entity.Resultat;
import com.ciblorgasport.competition.entity.StatutResultat;
import com.ciblorgasport.competition.entity.TypeClassement;
import com.ciblorgasport.competition.entity.TypeMedaille;
import com.ciblorgasport.competition.exception.BadRequestException;
import com.ciblorgasport.competition.exception.ConflictException;
import com.ciblorgasport.competition.exception.ResourceNotFoundException;
import com.ciblorgasport.competition.repository.ResultatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultatService {

    private final ResultatRepository resultatRepository;
    private final MancheService mancheService;
    private final ClassementService classementService;

    public ResultatService(ResultatRepository resultatRepository,
                           MancheService mancheService,
                           ClassementService classementService) {
        this.resultatRepository = resultatRepository;
        this.mancheService = mancheService;
        this.classementService = classementService;
    }

    public ResultatDto create(Long mancheId, CreateResultatRequest request) {
        if (request.athleteId() == null) {
            throw new BadRequestException("athleteId est obligatoire");
        }

        Manche manche = mancheService.getEntity(mancheId);

        resultatRepository.findByMancheIdAndAthleteId(mancheId, request.athleteId())
                .ifPresent(existing -> {
                    throw new ConflictException("Un résultat existe déjà pour cet athlète sur cette manche");
                });

        StatutResultat statut = request.statut() == null ? StatutResultat.VALIDE : request.statut();
        validateFieldsForStatus(manche.getTypeClassement(), statut, request.score(), request.temps());

        Resultat resultat = new Resultat();
        resultat.setManche(manche);
        resultat.setAthleteId(request.athleteId());
        resultat.setScore(request.score());
        resultat.setTemps(request.temps());
        resultat.setStatut(statut);
        resultat.setRang(null);
        resultat.setMedaille(TypeMedaille.AUCUNE);

        Resultat saved = resultatRepository.save(resultat);
        classementService.recalculerClassement(mancheId);

        Resultat refreshed = resultatRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));
        return classementService.toDto(refreshed);
    }

    public ResultatDto update(Long mancheId, Long resultatId, UpdateResultatRequest request) {
        Manche manche = mancheService.getEntity(mancheId);
        Resultat resultat = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));

        if (!resultat.getManche().getId().equals(manche.getId())) {
            throw new ResourceNotFoundException("Resultat not found for this manche");
        }

        StatutResultat statut = request.statut() == null ? resultat.getStatut() : request.statut();
        validateFieldsForStatus(
                manche.getTypeClassement(),
                statut,
                request.score() == null ? resultat.getScore() : request.score(),
                request.temps() == null ? resultat.getTemps() : request.temps()
        );

        if (request.score() != null) {
            resultat.setScore(request.score());
        }
        if (request.temps() != null) {
            resultat.setTemps(request.temps());
        }
        resultat.setStatut(statut);

        resultatRepository.save(resultat);
        classementService.recalculerClassement(mancheId);

        Resultat refreshed = resultatRepository.findById(resultatId)
                .orElseThrow(() -> new ResourceNotFoundException("Resultat not found"));
        return classementService.toDto(refreshed);
    }

    public List<ResultatDto> list(Long mancheId) {
        mancheService.getEntity(mancheId);
        return resultatRepository.findByMancheId(mancheId).stream()
                .map(classementService::toDto)
                .toList();
    }

    private void validateFieldsForStatus(TypeClassement typeClassement,
                                         StatutResultat statut,
                                         java.math.BigDecimal score,
                                         java.time.LocalTime temps) {
        if (statut != StatutResultat.VALIDE) {
            return;
        }

        if (typeClassement == TypeClassement.TIME_ASC && temps == null) {
            throw new BadRequestException("temps est obligatoire pour une manche TIME_ASC");
        }

        if (typeClassement == TypeClassement.SCORE_DESC && score == null) {
            throw new BadRequestException("score est obligatoire pour une manche SCORE_DESC");
        }
    }
}
