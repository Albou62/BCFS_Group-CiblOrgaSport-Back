package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.ClassementDto;
import com.ciblorgasport.competition.dto.CreateResultatRequest;
import com.ciblorgasport.competition.dto.PodiumDto;
import com.ciblorgasport.competition.dto.ResultatDto;
import com.ciblorgasport.competition.dto.UpdateResultatRequest;
import com.ciblorgasport.competition.entity.Competition;
import com.ciblorgasport.competition.entity.Epreuve;
import com.ciblorgasport.competition.entity.Manche;
import com.ciblorgasport.competition.entity.StatutResultat;
import com.ciblorgasport.competition.entity.TypeClassement;
import com.ciblorgasport.competition.entity.TypeMedaille;
import com.ciblorgasport.competition.exception.ConflictException;
import com.ciblorgasport.competition.repository.CompetitionRepository;
import com.ciblorgasport.competition.repository.EpreuveRepository;
import com.ciblorgasport.competition.repository.MancheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ResultatIntegrationTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EpreuveRepository epreuveRepository;

    @Autowired
    private MancheRepository mancheRepository;

    @Autowired
    private ResultatService resultatService;

    @Autowired
    private ClassementService classementService;

    private Long mancheTimeId;

    @BeforeEach
    void setUp() {
        Competition competition = new Competition();
        competition.setName("Competition test");
        competition.setDateDebut(LocalDate.of(2026, 1, 1));
        competition.setDateFin(LocalDate.of(2026, 1, 2));
        competition.setFinished(false);
        competition = competitionRepository.save(competition);

        Epreuve epreuve = new Epreuve();
        epreuve.setName("100m");
        epreuve.setCompetition(competition);
        epreuve = epreuveRepository.save(epreuve);

        Manche manche = new Manche();
        manche.setName("Finale");
        manche.setEpreuve(epreuve);
        manche.setTypeClassement(TypeClassement.TIME_ASC);
        manche.setOrdre(1);
        manche = mancheRepository.save(manche);

        mancheTimeId = manche.getId();
    }

    @Test
    void createUpdateAndReadClassementAndPodium() {
        ResultatDto r1 = resultatService.create(mancheTimeId,
                new CreateResultatRequest(1L, new BigDecimal("9.900"), LocalTime.of(0, 0, 12), StatutResultat.VALIDE));
        ResultatDto r2 = resultatService.create(mancheTimeId,
                new CreateResultatRequest(2L, new BigDecimal("9.700"), LocalTime.of(0, 0, 11), StatutResultat.VALIDE));
        ResultatDto r3 = resultatService.create(mancheTimeId,
                new CreateResultatRequest(3L, new BigDecimal("8.000"), LocalTime.of(0, 0, 13), StatutResultat.DNF));

        ClassementDto classement = classementService.getClassement(mancheTimeId);
        assertThat(classement.resultats()).hasSize(3);

        ResultatDto first = classement.resultats().get(0);
        assertThat(first.athleteId()).isEqualTo(2L);
        assertThat(first.rang()).isEqualTo(1);
        assertThat(first.medaille()).isEqualTo(TypeMedaille.OR);

        ResultatDto third = classement.resultats().stream()
                .filter(r -> r.athleteId().equals(3L))
                .findFirst()
                .orElseThrow();
        assertThat(third.rang()).isNull();
        assertThat(third.medaille()).isEqualTo(TypeMedaille.AUCUNE);

        ResultatDto updated = resultatService.update(mancheTimeId, r1.id(),
                new UpdateResultatRequest(new BigDecimal("10.100"), LocalTime.of(0, 0, 10), StatutResultat.VALIDE));
        assertThat(updated.rang()).isEqualTo(1);

        PodiumDto podium = classementService.getPodium(mancheTimeId);
        assertThat(podium.orResultat()).isNotNull();
        assertThat(podium.orResultat().athleteId()).isEqualTo(1L);
        assertThat(podium.argentResultat()).isNotNull();
    }

    @Test
    void duplicateAthleteOnSameMancheShouldFail() {
        resultatService.create(mancheTimeId,
                new CreateResultatRequest(1L, new BigDecimal("9.900"), LocalTime.of(0, 0, 12), StatutResultat.VALIDE));

        assertThatThrownBy(() -> resultatService.create(mancheTimeId,
                new CreateResultatRequest(1L, new BigDecimal("9.500"), LocalTime.of(0, 0, 11), StatutResultat.VALIDE)))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void noValidResultShouldReturnEmptyPodium() {
        resultatService.create(mancheTimeId,
                new CreateResultatRequest(1L, new BigDecimal("9.900"), LocalTime.of(0, 0, 12), StatutResultat.DNF));

        PodiumDto podium = classementService.getPodium(mancheTimeId);
        assertThat(podium.orResultat()).isNull();
        assertThat(podium.argentResultat()).isNull();
        assertThat(podium.bronzeResultat()).isNull();

        List<ResultatDto> resultats = resultatService.list(mancheTimeId);
        assertThat(resultats).hasSize(1);
        assertThat(resultats.get(0).rang()).isNull();
    }
}
