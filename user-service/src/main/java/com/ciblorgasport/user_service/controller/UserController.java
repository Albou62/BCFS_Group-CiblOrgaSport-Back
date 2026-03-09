package com.ciblorgasport.user_service.controller;

import com.ciblorgasport.user_service.entity.*;
import com.ciblorgasport.user_service.service.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired private SportifService sportifService;
    @Autowired private DocumentService documentService;
    @Autowired private TaskService taskService;
    @Autowired private TicketService ticketService;

    private String extractUsername(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            org.json.JSONObject json = new org.json.JSONObject(payload);
            return json.getString("sub"); 
        } catch (Exception e) {
            return null;
        }
    }

    // --- SPORTIF ---
    
    @GetMapping("/sportif/me")
    public SportifProfile getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        if (username == null) throw new RuntimeException("Utilisateur non identifié");
        return sportifService.getProfile(username);
    }

    @PostMapping("/documents")
    public Document upload(@RequestBody Document doc, @RequestHeader("Authorization") String authHeader) {
        String username = extractUsername(authHeader);
        if (username == null) throw new RuntimeException("Utilisateur non identifié");
        
        return documentService.uploadDocument(doc, username);
    }

    @GetMapping("/sportifs/{athleteId}/eligibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable Long athleteId) {
        return ResponseEntity.ok(sportifService.checkEligibility(athleteId));
    }

    // --- VOLONTAIRE & SPECTATEUR (Même logique) ---

    @GetMapping("/tasks")
    public List<Task> getMyTasks(@RequestHeader("Authorization") String authHeader) {
        return taskService.getTasksForVolunteer(extractUsername(authHeader));
    }

    @GetMapping("/tickets")
    public List<Ticket> getMyTickets(@RequestHeader("Authorization") String authHeader) {
        return ticketService.getTicketsForUser(extractUsername(authHeader));
    }

    @PostMapping("/tickets")
    public Ticket addTicket(@RequestHeader("Authorization") String authHeader, @RequestBody Ticket ticket) {
        return ticketService.addTicket(ticket, extractUsername(authHeader));
    }
}