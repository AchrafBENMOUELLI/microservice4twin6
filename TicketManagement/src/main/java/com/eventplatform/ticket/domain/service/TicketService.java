package com.eventplatform.ticket.domain.service;

import com.eventplatform.ticket.application.client.EventClient;
import com.eventplatform.ticket.application.client.UserClient;
import com.eventplatform.ticket.application.dto.EventDTO;
import com.eventplatform.ticket.application.dto.UserDTO;
import com.eventplatform.ticket.domain.model.Ticket;
import com.eventplatform.ticket.domain.port.TicketRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    public Ticket createTicket(Ticket ticket) {
        // Validate user exists
        try {
            UserDTO user = userClient.getUserById(ticket.getUserId());
            if (user == null) {
                throw new RuntimeException("User not found with id: " + ticket.getUserId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("User not found with id: " + ticket.getUserId());
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with User service: " + e.getMessage());
        }
        
        // Validate event exists
        try {
            EventDTO event = eventClient.getEventById(ticket.getEventId());
            if (event == null) {
                throw new RuntimeException("Event not found with id: " + ticket.getEventId());
            }
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Event not found with id: " + ticket.getEventId());
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with Event service: " + e.getMessage());
        }
        
        ticket.setTicketCode(UUID.randomUUID().toString());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setStatus("ACTIVE");
        return ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket updateTicket(Long id, Ticket updatedTicket) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        // Update only allowed fields
        existingTicket.setTicketType(updatedTicket.getTicketType());
        existingTicket.setPrice(updatedTicket.getPrice());
        existingTicket.setPurchaserEmail(updatedTicket.getPurchaserEmail());

        return ticketRepository.save(existingTicket);
    }
    
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket not found with id: " + id);
        }
        ticketRepository.deleteById(id);
    }
}
