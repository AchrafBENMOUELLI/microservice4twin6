package com.eventplatform.ticket.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private String ticketCode;
    private String ticketType;
    private BigDecimal price;
    private String status;
    private LocalDateTime purchaseDate;
    private String purchaserEmail;
    private Long userId;
    private Long eventId;
    private UserDTO user;
    private EventDTO event;
}
