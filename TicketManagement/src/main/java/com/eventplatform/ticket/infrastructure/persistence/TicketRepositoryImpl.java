package com.eventplatform.ticket.infrastructure.persistence;

import com.eventplatform.ticket.domain.model.Ticket;
import com.eventplatform.ticket.domain.port.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {
    private final JpaTicketRepository jpaTicketRepository;

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = toEntity(ticket);
        TicketEntity saved = jpaTicketRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return jpaTicketRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ticket> findAll() {
        return jpaTicketRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaTicketRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaTicketRepository.existsById(id);
    }

    private TicketEntity toEntity(Ticket ticket) {
        return TicketEntity.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .ticketType(ticket.getTicketType())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .purchaseDate(ticket.getPurchaseDate())
                .purchaserEmail(ticket.getPurchaserEmail())
                .userId(ticket.getUserId())
                .eventId(ticket.getEventId())
                .build();
    }

    private Ticket toDomain(TicketEntity entity) {
        return Ticket.builder()
                .id(entity.getId())
                .ticketCode(entity.getTicketCode())
                .ticketType(entity.getTicketType())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .purchaseDate(entity.getPurchaseDate())
                .purchaserEmail(entity.getPurchaserEmail())
                .userId(entity.getUserId())
                .eventId(entity.getEventId())
                .build();
    }
}
