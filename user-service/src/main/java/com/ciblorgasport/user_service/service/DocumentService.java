package com.ciblorgasport.user_service.service;

import com.ciblorgasport.user_service.entity.Document;
import com.ciblorgasport.user_service.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentService {
    @Autowired private DocumentRepository documentRepo;

    // Suppression du Long authId, on utilise uniquement le username
    public Document uploadDocument(Document doc, String username) {
        doc.setUploaderUsername(username); // Lien avec le token
        doc.setAthleteName(username);      // Nom affiché pour le commissaire
        doc.setStatus("EN_ATTENTE");
        return documentRepo.save(doc);
    }

    public List<Document> getPendingDocuments() {
        return documentRepo.findByStatus("EN_ATTENTE");
    }

    public Document reviewDocument(Long docId, String status) {
        Document doc = documentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        doc.setStatus(status); // "VALIDE" ou "REFUSE"
        return documentRepo.save(doc);
    }
}