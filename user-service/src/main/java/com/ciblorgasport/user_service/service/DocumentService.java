package com.ciblorgasport.user_service.service;

import com.ciblorgasport.user_service.entity.Document;
import com.ciblorgasport.user_service.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentService {
    @Autowired private DocumentRepository documentRepo;

    public Document uploadDocument(Document doc, String username) {
        String cleanUsername = username.toLowerCase().trim();
        
        List<Document> existingDocs = documentRepo.findByUploaderUsername(cleanUsername);
        
        Document docToSave = doc;
        
        for (Document existing : existingDocs) {
            if (existing.getType().equalsIgnoreCase(doc.getType())) {
                docToSave = existing; 
                docToSave.setFileName(doc.getFileName()); 
                break;
            }
        }

        docToSave.setUploaderUsername(cleanUsername); 
        docToSave.setAthleteName(cleanUsername);     
        docToSave.setStatus("EN_ATTENTE"); 
        
        return documentRepo.save(docToSave);
    }

    public List<Document> getPendingDocuments() {
        return documentRepo.findByStatus("EN_ATTENTE");
    }

    public Document reviewDocument(Long docId, String status) {
        Document doc = documentRepo.findById(docId)
                .orElseThrow(() -> new RuntimeException("Document introuvable"));
        doc.setStatus(status); 
        return documentRepo.save(doc);
    }
    
    public List<Document> getDocumentsByUser(String username) {
        return documentRepo.findByUploaderUsername(username);
    }
}