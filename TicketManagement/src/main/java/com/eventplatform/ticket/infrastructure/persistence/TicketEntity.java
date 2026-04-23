package com.eventplatform.ticket.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String ticketCode;
    
    @Column(nullable = false)
    private String ticketType;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private LocalDateTime purchaseDate;
    
    @Column(nullable = false)
    private String purchaserEmail;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long eventId;
}
