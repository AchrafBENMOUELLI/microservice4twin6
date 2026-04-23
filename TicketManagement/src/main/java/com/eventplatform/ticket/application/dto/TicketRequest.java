package com.eventplatform.ticket.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    @NotBlank(message = "Ticket type is required")
    private String ticketType;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotBlank(message = "Purchaser email is required")
    @Email(message = "Invalid email format")
    private String purchaserEmail;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
}
