package com.ciblorgasport.user_service.controller;

import com.ciblorgasport.user_service.entity.*;
import com.ciblorgasport.user_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private DocumentService documentService;
    @Autowired private TaskService taskService;
    @Autowired private SportifService sportifService;

    // --- COMMISSAIRE (Arthur) ---
    @GetMapping("/documents")
    public List<Document> getPendingDocs() {
        return documentService.getPendingDocuments();
    }

    @PutMapping("/documents/{id}/review")
    public Document reviewDoc(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return documentService.reviewDocument(id, payload.get("status"));
    }
    
    @PostMapping("/sportif/inscrire")
    public ResponseEntity<?> registerAthleteToEpreuve(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long epreuveId = Long.parseLong(payload.get("epreuveId").toString());

        try {
            SportifProfile updatedProfile = sportifService.registerToEpreuve(username, epreuveId);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- RESPONSABLE (Marius) ---
    @GetMapping("/volontaires")
    public ResponseEntity<List<VolontaireProfile>> getVolunteers() {
        List<VolontaireProfile> profils = taskService.getAllVolunteersWithTasks();
        return ResponseEntity.ok(profils);
    }
    
    @PostMapping("/tasks/assign")
    public Task assignTask(@RequestBody Map<String, Object> payload) {
        Object rawId = payload.get("volunteerId");
        if (rawId == null) rawId = payload.get("userId");
        
        if (rawId == null || payload.get("taskName") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID du volontaire ou nom de tâche manquant");
        }

        Long volId = Long.parseLong(rawId.toString());
        String username = (String) payload.get("username");
        String title = (String) payload.get("taskName");
        String timeSlot = (String) payload.get("timeSlot"); 
        
        return taskService.assignTask(volId, username, title, timeSlot);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body("Le statut est obligatoire");
        }
        taskService.updateTaskStatus(id, newStatus);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/epreuves/{id}/participants")
    public ResponseEntity<List<SportifProfile>> getParticipants(@PathVariable Long id) {
        return ResponseEntity.ok(sportifService.getInscrits(id));
    }
    
    @DeleteMapping("/epreuves/{epreuveId}/participants/{username}")
    public ResponseEntity<?> removeAthleteFromEpreuve(@PathVariable Long epreuveId, @PathVariable String username) {
        try {
            sportifService.unregisterFromEpreuve(username, epreuveId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la désinscription");
        }
    }
    
}