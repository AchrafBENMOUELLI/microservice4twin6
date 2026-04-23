package com.esprit.microservice.feedbackproject.dto;

import java.util.Date;
import java.util.List;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Date datebirth;
    private String street;
    private String city;
    private String state;
    private String zip;
    private List<String> phones;

    public UserDTO() {}

    public UserDTO(Long id, String firstName, String lastName, String email, String role,
                   Date datebirth, String street, String city, String state, String zip, List<String> phones) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.datebirth = datebirth;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phones = phones;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getDatebirth() { return datebirth; }
    public void setDatebirth(Date datebirth) { this.datebirth = datebirth; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }

    public List<String> getPhones() { return phones; }
    public void setPhones(List<String> phones) { this.phones = phones; }
}
