package com.eventplatform.ticket.domain.port;

import com.eventplatform.ticket.domain.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(Long id);
    List<Ticket> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
