package com.eventplatform.blog.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
