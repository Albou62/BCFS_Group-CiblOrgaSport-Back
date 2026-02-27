package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.ClassementDto;
import com.ciblorgasport.competition.dto.PodiumDto;
import com.ciblorgasport.competition.dto.ResultatDto;
import com.ciblorgasport.competition.entity.Manche;
import com.ciblorgasport.competition.entity.Resultat;
import com.ciblorgasport.competition.entity.StatutResultat;
import com.ciblorgasport.competition.entity.TypeClassement;
import com.ciblorgasport.competition.entity.TypeMedaille;
import com.ciblorgasport.competition.repository.ResultatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ClassementService {

    private final ResultatRepository resultatRepository;
    private final MancheService mancheService;

    public ClassementService(ResultatRepository resultatRepository,
                             MancheService mancheService) {
        this.resultatRepository = resultatRepository;
        this.mancheService = mancheService;
    }

    @Transactional
    public void recalculerClassement(Long mancheId) {
        Manche manche = mancheService.getEntity(mancheId);
        List<Resultat> all = resultatRepository.findByMancheId(mancheId);

        List<Resultat> valides = all.stream()
                .filter(r -> r.getStatut() == StatutResultat.VALIDE)
                .sorted(comparatorFor(manche.getTypeClassement()))
                .toList();

        assignRanksAndMedals(valides, manche.getTypeClassement());

        all.stream()
                .filter(r -> r.getStatut() != StatutResultat.VALIDE)
                .forEach(r -> {
                    r.setRang(null);
                    r.setMedaille(TypeMedaille.AUCUNE);
                });

        resultatRepository.saveAll(all);
    }

    public ClassementDto getClassement(Long mancheId) {
        Manche manche = mancheService.getEntity(mancheId);
        List<ResultatDto> resultats = resultatRepository.findByMancheId(mancheId).stream()
                .sorted((a, b) -> compareForDisplay(a, b, manche.getTypeClassement()))
                .map(this::toDto)
                .toList();
        return new ClassementDto(mancheId, manche.getTypeClassement(), resultats);
    }

    public PodiumDto getPodium(Long mancheId) {
        mancheService.getEntity(mancheId);
        List<ResultatDto> resultats = resultatRepository.findByMancheId(mancheId).stream()
                .filter(r -> r.getStatut() == StatutResultat.VALIDE)
                .filter(r -> r.getRang() != null)
                .map(this::toDto)
                .toList();

        ResultatDto orResultat = firstByRank(resultats, 1);
        ResultatDto argentResultat = firstByRank(resultats, 2);
        ResultatDto bronzeResultat = firstByRank(resultats, 3);

        return new PodiumDto(mancheId, orResultat, argentResultat, bronzeResultat);
    }

    private ResultatDto firstByRank(List<ResultatDto> resultats, int rank) {
        return resultats.stream().filter(r -> Objects.equals(r.rang(), rank)).findFirst().orElse(null);
    }

    private void assignRanksAndMedals(List<Resultat> sorted, TypeClassement typeClassement) {
        Resultat previous = null;
        int position = 0;
        int rank = 0;

        for (Resultat current : sorted) {
            position++;
            if (previous == null) {
                rank = 1;
            } else if (!sameKeys(previous, current, typeClassement)) {
                rank = position;
            }

            current.setRang(rank);
            current.setMedaille(medalFromRank(rank));
            previous = current;
        }
    }

    private Comparator<Resultat> comparatorFor(TypeClassement typeClassement) {
        Comparator<LocalTime> timeComparator = Comparator.nullsLast(Comparator.naturalOrder());
        Comparator<BigDecimal> scoreComparator = Comparator.nullsLast(Comparator.naturalOrder());

        if (typeClassement == TypeClassement.TIME_ASC) {
            return Comparator.comparing(Resultat::getTemps, timeComparator)
                    .thenComparing(Resultat::getScore, scoreComparator.reversed())
                    .thenComparing(Resultat::getId, Comparator.nullsLast(Comparator.naturalOrder()));
        }

        return Comparator.comparing(Resultat::getScore, scoreComparator.reversed())
                .thenComparing(Resultat::getTemps, timeComparator)
                .thenComparing(Resultat::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private int compareForDisplay(Resultat left, Resultat right, TypeClassement typeClassement) {
        if (left.getRang() == null && right.getRang() != null) {
            return 1;
        }
        if (left.getRang() != null && right.getRang() == null) {
            return -1;
        }
        if (left.getRang() != null && right.getRang() != null) {
            int byRank = Integer.compare(left.getRang(), right.getRang());
            if (byRank != 0) {
                return byRank;
            }
        }
        return comparatorFor(typeClassement).compare(left, right);
    }

    private boolean sameKeys(Resultat previous, Resultat current, TypeClassement typeClassement) {
        if (typeClassement == TypeClassement.TIME_ASC) {
            return Objects.equals(previous.getTemps(), current.getTemps())
                    && Objects.equals(previous.getScore(), current.getScore());
        }
        return Objects.equals(previous.getScore(), current.getScore())
                && Objects.equals(previous.getTemps(), current.getTemps());
    }

    private TypeMedaille medalFromRank(Integer rank) {
        if (rank == null) {
            return TypeMedaille.AUCUNE;
        }
        return switch (rank) {
            case 1 -> TypeMedaille.OR;
            case 2 -> TypeMedaille.ARGENT;
            case 3 -> TypeMedaille.BRONZE;
            default -> TypeMedaille.AUCUNE;
        };
    }

    public ResultatDto toDto(Resultat resultat) {
        return new ResultatDto(
                resultat.getId(),
                resultat.getAthleteId(),
                resultat.getManche() != null ? resultat.getManche().getId() : null,
                resultat.getScore(),
                resultat.getTemps(),
                resultat.getRang(),
                resultat.getStatut(),
                resultat.getMedaille()
        );
    }
}
