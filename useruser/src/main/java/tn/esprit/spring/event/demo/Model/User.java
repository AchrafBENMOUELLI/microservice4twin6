package tn.esprit.spring.event.demo.Model;

import java.util.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Temporal(TemporalType.DATE)
    private Date datebirth;

    // Address fields directly in User
    private String street;
    private String city;
    private String state;
    private String zip;

    @ElementCollection
    @CollectionTable(name = "user_phones", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone")
    private List<String> phones;

    // Connected status
    @Transient
    private boolean connected = false;

    @ElementCollection
    @CollectionTable(
            name = "user_favorite_events",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "event_id")
    private Set<Long> favoriteEvents = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_purchased_tickets",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "ticket_id")
    private Set<Long> purchasedTickets = new HashSet<>();
}