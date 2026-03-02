package com.ciblorgasport.user_service.service;

import com.ciblorgasport.user_service.entity.Document;
import com.ciblorgasport.user_service.entity.SportifProfile;
import com.ciblorgasport.user_service.repository.DocumentRepository;
import com.ciblorgasport.user_service.repository.SportifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SportifService {
    @Autowired private SportifRepository sportifRepo;
    @Autowired private DocumentRepository documentRepo;

    // Récupération du profil sportif par le nom d'utilisateur (badge JWT)
    public SportifProfile getProfile(String username) {
        return sportifRepo.findByUsername(username);
    }

    public boolean checkEligibility(Long athleteId) {
        SportifProfile sportif = sportifRepo.findById(athleteId).orElse(null);
        if (sportif == null) return false;

        // Règle métier : Vérification des documents par le username du profil
        List<Document> docs = documentRepo.findByUploaderUsername(sportif.getUsername());
        
        boolean validPass = docs.stream()
                .anyMatch(d -> "Passeport".equals(d.getType()) && "VALIDE".equals(d.getStatus()));
        boolean validCertif = docs.stream()
                .anyMatch(d -> "Certificat".equals(d.getType()) && "VALIDE".equals(d.getStatus()));
        
        // Un sportif est éligible si ses 2 docs sont valides ET qu'il a accepté le tracking
        return validPass && validCertif && sportif.isTrackingAccepted();
    }
}