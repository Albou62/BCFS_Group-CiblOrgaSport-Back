package com.ciblorgasport.user_service.controller;

import com.ciblorgasport.user_service.entity.*;
import com.ciblorgasport.user_service.service.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired private SportifService sportifService;
    @Autowired private DocumentService documentService;
    @Autowired private TaskService taskService;
    @Autowired private TicketService ticketService;

    // Méthode unique et réelle pour identifier l'utilisateur
    private String extractUsername(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            String token = authHeader.substring(7);
            String[] parts = token.split("\\.");
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            org.json.JSONObject json = new org.json.JSONObject(payload);
            return json.getString("sub"); // "sub" contient le username dans votre JwtService
        } catch (Exception e) {
            return null;
        }
    }

    // --- SPORTIF ---
    @GetMapping("/sportif/me")
    public SportifProfile getMyProfile(@RequestHeader("Authorization") String token) {
        return sportifService.getProfile(extractUsername(token));
    }

    @PostMapping("/documents")
    public Document uploadDoc(@RequestHeader("Authorization") String token, @RequestBody Document doc) {
        // Le service DocumentService doit être adapté pour accepter le username
        return documentService.uploadDocument(doc, extractUsername(token));
    }

    @GetMapping("/sportifs/{athleteId}/eligibility")
    public ResponseEntity<Boolean> checkEligibility(@PathVariable Long athleteId) {
        return ResponseEntity.ok(sportifService.checkEligibility(athleteId));
    }

    // --- VOLONTAIRE ---
    @GetMapping("/tasks")
    public List<Task> getMyTasks(@RequestHeader("Authorization") String authHeader) {
        return taskService.getTasksForVolunteer(extractUsername(authHeader));
    }

    // --- SPECTATEUR ---
    @GetMapping("/tickets")
    public List<Ticket> getMyTickets(@RequestHeader("Authorization") String token) {
        return ticketService.getTicketsForUser(extractUsername(token));
    }

    @PostMapping("/tickets")
    public Ticket addTicket(@RequestHeader("Authorization") String token, @RequestBody Ticket ticket) {
        return ticketService.addTicket(ticket, extractUsername(token));
    }
}