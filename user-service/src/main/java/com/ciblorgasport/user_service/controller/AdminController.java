package com.ciblorgasport.user_service.controller;

import com.ciblorgasport.user_service.entity.*;
import com.ciblorgasport.user_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private DocumentService documentService;
    @Autowired private TaskService taskService;

    // --- COMMISSAIRE (Arthur) ---
    @GetMapping("/documents")
    public List<Document> getPendingDocs() {
        return documentService.getPendingDocuments();
    }

    @PutMapping("/documents/{id}/review")
    public Document reviewDoc(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        return documentService.reviewDocument(id, payload.get("status"));
    }

    // --- RESPONSABLE (Marius) ---
    @GetMapping("/volontaires")
    public List<VolontaireProfile> getAllVolunteers() {
        return taskService.getAllVolunteers();
    }

    @PostMapping("/tasks/assign")
    public Task assignTask(@RequestBody Map<String, Object> payload) {
        // On vérifie si les clés existent avant de faire .toString()
        if (payload.get("volunteerId") == null || payload.get("taskName") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Données manquantes");
        }

        Long volId = Long.parseLong(payload.get("volunteerId").toString());
        String username = (String) payload.get("username");
        String title = (String) payload.get("taskName");
        String timeSlot = (String) payload.get("timeSlot"); // Récupère l'heure de l'input type="time"
        
        return taskService.assignTask(volId, username, title, timeSlot);
    }}