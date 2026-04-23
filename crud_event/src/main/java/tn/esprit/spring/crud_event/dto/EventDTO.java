package tn.esprit.spring.crud_event.dto;

import java.time.LocalDate;

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

    public EventDTO() {}

    public EventDTO(Long id, String title, String description, LocalDate date, String location,
                    Double price, Long organizerid, String imageUrl, Integer nbplaces, Integer nblikes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.price = price;
        this.organizerid = organizerid;
        this.imageUrl = imageUrl;
        this.nbplaces = nbplaces;
        this.nblikes = nblikes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Long getOrganizerid() { return organizerid; }
    public void setOrganizerid(Long organizerid) { this.organizerid = organizerid; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getNbplaces() { return nbplaces; }
    public void setNbplaces(Integer nbplaces) { this.nbplaces = nbplaces; }

    public Integer getNblikes() { return nblikes; }
    public void setNblikes(Integer nblikes) { this.nblikes = nblikes; }
}
