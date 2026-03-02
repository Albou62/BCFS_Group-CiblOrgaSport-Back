package com.ciblorgasport.user_service.service;

import com.ciblorgasport.user_service.entity.Ticket;
import com.ciblorgasport.user_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketService {
    @Autowired private TicketRepository ticketRepo;

    // On utilise désormais le username extrait du Token
    public Ticket addTicket(Ticket ticket, String username) {
        ticket.setOwnerUsername(username); 
        return ticketRepo.save(ticket);
    }

    public List<Ticket> getTicketsForUser(String username) {
        return ticketRepo.findByOwnerUsername(username);
    }
}