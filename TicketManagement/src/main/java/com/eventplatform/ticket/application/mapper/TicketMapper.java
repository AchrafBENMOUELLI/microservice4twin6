package com.eventplatform.ticket.application.mapper;

import com.eventplatform.ticket.application.dto.TicketRequest;
import com.eventplatform.ticket.application.dto.TicketResponse;
import com.eventplatform.ticket.domain.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    
    public Ticket toDomain(TicketRequest request) {
        return Ticket.builder()
                .ticketType(request.getTicketType())
                .price(request.getPrice())
                .purchaserEmail(request.getPurchaserEmail())
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .build();
    }

    public TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
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
}
