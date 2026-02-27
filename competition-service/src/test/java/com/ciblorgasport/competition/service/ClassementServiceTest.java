package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.entity.Manche;
import com.ciblorgasport.competition.entity.Resultat;
import com.ciblorgasport.competition.entity.StatutResultat;
import com.ciblorgasport.competition.entity.TypeClassement;
import com.ciblorgasport.competition.entity.TypeMedaille;
import com.ciblorgasport.competition.repository.ResultatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassementServiceTest {

    @Mock
    private ResultatRepository resultatRepository;

    @Mock
    private MancheService mancheService;

    @InjectMocks
    private ClassementService classementService;

    @Captor
    private ArgumentCaptor<List<Resultat>> resultatsCaptor;

    @Test
    void recalculerClassement_timeAsc_handlesTieAndInvalidStatuses() {
        Manche manche = new Manche();
        manche.setId(10L);
        manche.setTypeClassement(TypeClassement.TIME_ASC);

        Resultat r1 = resultat(1L, 100L, new BigDecimal("10.000"), LocalTime.of(0, 1, 0), StatutResultat.VALIDE);
        Resultat r2 = resultat(2L, 101L, new BigDecimal("9.000"), LocalTime.of(0, 1, 0), StatutResultat.VALIDE);
        Resultat r3 = resultat(3L, 102L, new BigDecimal("9.000"), LocalTime.of(0, 1, 5), StatutResultat.VALIDE);
        Resultat r4 = resultat(4L, 103L, new BigDecimal("8.000"), LocalTime.of(0, 2, 0), StatutResultat.DNF);

        when(mancheService.getEntity(10L)).thenReturn(manche);
        when(resultatRepository.findByMancheId(10L)).thenReturn(List.of(r1, r2, r3, r4));
        when(resultatRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        classementService.recalculerClassement(10L);

        verify(resultatRepository).saveAll(resultatsCaptor.capture());
        List<Resultat> saved = resultatsCaptor.getValue();

        Resultat savedR1 = byId(saved, 1L);
        Resultat savedR2 = byId(saved, 2L);
        Resultat savedR3 = byId(saved, 3L);
        Resultat savedR4 = byId(saved, 4L);

        assertThat(savedR1.getRang()).isEqualTo(1);
        assertThat(savedR1.getMedaille()).isEqualTo(TypeMedaille.OR);

        assertThat(savedR2.getRang()).isEqualTo(2);
        assertThat(savedR2.getMedaille()).isEqualTo(TypeMedaille.ARGENT);

        assertThat(savedR3.getRang()).isEqualTo(3);
        assertThat(savedR3.getMedaille()).isEqualTo(TypeMedaille.BRONZE);

        assertThat(savedR4.getRang()).isNull();
        assertThat(savedR4.getMedaille()).isEqualTo(TypeMedaille.AUCUNE);
    }

    @Test
    void recalculerClassement_scoreDesc_competitionRankWhenTie() {
        Manche manche = new Manche();
        manche.setId(11L);
        manche.setTypeClassement(TypeClassement.SCORE_DESC);

        Resultat r1 = resultat(11L, 200L, new BigDecimal("15.500"), LocalTime.of(0, 1, 30), StatutResultat.VALIDE);
        Resultat r2 = resultat(12L, 201L, new BigDecimal("15.500"), LocalTime.of(0, 1, 30), StatutResultat.VALIDE);
        Resultat r3 = resultat(13L, 202L, new BigDecimal("14.000"), LocalTime.of(0, 1, 10), StatutResultat.VALIDE);

        when(mancheService.getEntity(11L)).thenReturn(manche);
        when(resultatRepository.findByMancheId(11L)).thenReturn(List.of(r1, r2, r3));
        when(resultatRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        classementService.recalculerClassement(11L);

        verify(resultatRepository).saveAll(resultatsCaptor.capture());
        List<Resultat> saved = resultatsCaptor.getValue();

        assertThat(byId(saved, 11L).getRang()).isEqualTo(1);
        assertThat(byId(saved, 12L).getRang()).isEqualTo(1);
        assertThat(byId(saved, 13L).getRang()).isEqualTo(3);

        assertThat(byId(saved, 11L).getMedaille()).isEqualTo(TypeMedaille.OR);
        assertThat(byId(saved, 12L).getMedaille()).isEqualTo(TypeMedaille.OR);
        assertThat(byId(saved, 13L).getMedaille()).isEqualTo(TypeMedaille.BRONZE);
    }

    private Resultat byId(List<Resultat> resultats, Long id) {
        return resultats.stream().filter(r -> r.getId().equals(id)).findFirst().orElseThrow();
    }

    private Resultat resultat(Long id,
                             Long athleteId,
                             BigDecimal score,
                             LocalTime temps,
                             StatutResultat statut) {
        Resultat resultat = new Resultat();
        resultat.setId(id);
        resultat.setAthleteId(athleteId);
        resultat.setScore(score);
        resultat.setTemps(temps);
        resultat.setStatut(statut);
        return resultat;
    }
}
