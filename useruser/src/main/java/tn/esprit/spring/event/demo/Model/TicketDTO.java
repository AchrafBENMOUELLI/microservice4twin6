package tn.esprit.spring.event.demo.Model;

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
public class TicketDTO {
  private Long id;
  private String ticketCode;
  private String ticketType;
  private BigDecimal price;
  private String status;
  private LocalDateTime purchaseDate;
  private String purchaserEmail;
}
