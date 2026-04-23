package tn.esprit.spring.event.demo.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
  private Long id;
  private String title;
  private String description;
  private LocalDate date;
  private String location;
  private Double price;
  private Long organizerid;
  private String imageUrl;
  private Integer nbplaces;
  private Integer nblikes;
}
