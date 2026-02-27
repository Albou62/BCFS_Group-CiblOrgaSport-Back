package com.ciblorgasport.competition.service;

import com.ciblorgasport.competition.dto.CreateMancheRequest;
import com.ciblorgasport.competition.dto.MancheDto;
import com.ciblorgasport.competition.entity.Epreuve;
import com.ciblorgasport.competition.entity.Manche;
import com.ciblorgasport.competition.exception.BadRequestException;
import com.ciblorgasport.competition.exception.ResourceNotFoundException;
import com.ciblorgasport.competition.repository.EpreuveRepository;
import com.ciblorgasport.competition.repository.MancheRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MancheService {

    private final MancheRepository mancheRepository;
    private final EpreuveRepository epreuveRepository;

    public MancheService(MancheRepository mancheRepository, EpreuveRepository epreuveRepository) {
        this.mancheRepository = mancheRepository;
        this.epreuveRepository = epreuveRepository;
    }

    public List<MancheDto> listByEpreuve(Long epreuveId) {
        ensureEpreuveExists(epreuveId);
        return mancheRepository.findByEpreuveIdOrderByOrdreAsc(epreuveId).stream()
                .map(this::toDto)
                .toList();
    }

    public MancheDto create(Long epreuveId, CreateMancheRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("Le nom de la manche est obligatoire");
        }
        if (request.typeClassement() == null) {
            throw new BadRequestException("Le type de classement est obligatoire");
        }

        Epreuve epreuve = epreuveRepository.findById(epreuveId)
                .orElseThrow(() -> new ResourceNotFoundException("Epreuve not found"));

        Manche manche = new Manche();
        manche.setName(request.name());
        manche.setTypeClassement(request.typeClassement());
        manche.setOrdre(request.ordre());
        manche.setEpreuve(epreuve);

        return toDto(mancheRepository.save(manche));
    }

    public Manche getEntity(Long mancheId) {
        return mancheRepository.findById(mancheId)
                .orElseThrow(() -> new ResourceNotFoundException("Manche not found"));
    }

    private void ensureEpreuveExists(Long epreuveId) {
        if (!epreuveRepository.existsById(epreuveId)) {
            throw new ResourceNotFoundException("Epreuve not found");
        }
    }

    private MancheDto toDto(Manche manche) {
        return new MancheDto(
                manche.getId(),
                manche.getName(),
                manche.getEpreuve() != null ? manche.getEpreuve().getId() : null,
                manche.getTypeClassement(),
                manche.getOrdre()
        );
    }
}
