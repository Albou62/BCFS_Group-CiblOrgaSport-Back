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

    public SportifProfile getProfile(String username) {
        SportifProfile sportif = sportifRepo.findByUsername(username);
        
       
        if (sportif == null) {
            sportif = new SportifProfile();
            sportif.setUsername(username);
            sportif.setTrackingAccepted(true);
            sportif = sportifRepo.save(sportif);
        }
        
        List<Document> docs = documentRepo.findByUploaderUsername(username);
        sportif.setDocuments(docs);
        
        return sportif;
    }
    public boolean checkEligibility(Long athleteId) {
        SportifProfile sportif = sportifRepo.findById(athleteId).orElse(null);
        if (sportif == null) return false;

        List<Document> docs = documentRepo.findByUploaderUsername(sportif.getUsername());
        
        boolean validPass = docs.stream()
                .anyMatch(d -> "Passeport".equals(d.getType()) && "VALIDE".equals(d.getStatus()));
        boolean validCertif = docs.stream()
                .anyMatch(d -> "Certificat".equals(d.getType()) && "VALIDE".equals(d.getStatus()));
        
        return validPass && validCertif && sportif.isTrackingAccepted();
    }
}