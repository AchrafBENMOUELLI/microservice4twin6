package com.eventplatform.ticket.application.controller;

import com.eventplatform.ticket.application.client.EventClient;
import com.eventplatform.ticket.application.client.UserClient;
import com.eventplatform.ticket.application.dto.EventDTO;
import com.eventplatform.ticket.application.dto.TicketRequest;
import com.eventplatform.ticket.application.dto.TicketResponse;
import com.eventplatform.ticket.application.dto.UserDTO;
import com.eventplatform.ticket.application.mapper.TicketMapper;
import com.eventplatform.ticket.domain.model.Ticket;
import com.eventplatform.ticket.domain.service.TicketService;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TicketController {

  private final TicketService ticketService;
  private final TicketMapper ticketMapper;
  private final UserClient userClient;
  private final EventClient eventClient;

  // ─── USER endpoints (role: user) ────────────────────────────

  @PostMapping("/api/tickets/user/add")
  public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
    Ticket ticket = ticketMapper.toDomain(request);
    Ticket created = ticketService.createTicket(ticket);
    return ResponseEntity.status(HttpStatus.CREATED).body(enrichTicketResponse(created));
  }

  @GetMapping("/api/tickets/user/{id}")
  public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {
    Ticket ticket = ticketService.getTicketById(id);
    return ResponseEntity.ok(enrichTicketResponse(ticket));
  }

  @GetMapping("/api/tickets/user/all")
  public ResponseEntity<List<TicketResponse>> getAllTickets() {
    List<TicketResponse> tickets = ticketService.getAllTickets().stream()
      .map(this::enrichTicketResponse)
      .collect(Collectors.toList());
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/api/tickets/user/by-user/{userId}")
  public ResponseEntity<List<TicketResponse>> getTicketsByUserId(@PathVariable Long userId) {
    List<TicketResponse> tickets = ticketService.getAllTickets().stream()
      .filter(t -> t.getUserId().equals(userId))
      .map(this::enrichTicketResponse)
      .collect(Collectors.toList());
    return ResponseEntity.ok(tickets);
  }

  @GetMapping("/api/tickets/user/by-event/{eventId}")
  public ResponseEntity<List<TicketResponse>> getTicketsByEventId(@PathVariable Long eventId) {
    List<TicketResponse> tickets = ticketService.getAllTickets().stream()
      .filter(t -> t.getEventId().equals(eventId))
      .map(this::enrichTicketResponse)
      .collect(Collectors.toList());
    return ResponseEntity.ok(tickets);
  }

  @PutMapping("/api/tickets/user/update/{id}")
  public ResponseEntity<TicketResponse> updateTicket(
    @PathVariable Long id,
    @Valid @RequestBody TicketRequest request) {
    Ticket ticket = ticketMapper.toDomain(request);
    Ticket updated = ticketService.updateTicket(id, ticket);
    return ResponseEntity.ok(enrichTicketResponse(updated));
  }

  // ─── ADMIN endpoints (role: admin) ──────────────────────────

  @DeleteMapping("/api/tickets/admin/delete/{id}")
  public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
    ticketService.deleteTicket(id);
    return ResponseEntity.noContent().build();
  }

  // ─── PUBLIC (authenticated only) ────────────────────────────

  @Value("${welcome.message}")
  private String welcomeMessage;

  @GetMapping("/api/tickets/welcome")
  public String welcome() {
    return welcomeMessage;
  }

  // ─── Helper ─────────────────────────────────────────────────

  private TicketResponse enrichTicketResponse(Ticket ticket) {
    TicketResponse response = ticketMapper.toResponse(ticket);
    try {
      UserDTO user = userClient.getUserById(ticket.getUserId());
      response.setUser(user);
    } catch (FeignException e) {
      response.setUser(null);
    }
    try {
      EventDTO event = eventClient.getEventById(ticket.getEventId());
      response.setEvent(event);
    } catch (FeignException e) {
      response.setEvent(null);
    }
    return response;
  }
}
