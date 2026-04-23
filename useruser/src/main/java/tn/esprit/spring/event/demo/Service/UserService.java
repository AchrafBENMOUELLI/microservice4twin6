package tn.esprit.spring.event.demo.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import tn.esprit.spring.event.demo.Model.*;
import tn.esprit.spring.event.demo.Repository.UserRepository;
import tn.esprit.spring.event.demo.feign.EventClient;
import tn.esprit.spring.event.demo.feign.TicketClient;
import tn.esprit.spring.event.demo.dto.UserDTO;
import tn.esprit.spring.event.demo.messaging.UserProducer;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserProducer userProducer;

    @Autowired
    private EventClient eventClient;

    @Autowired
    private TicketClient ticketClient;

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    // Store connected user ID (in production, use session or cache)
    private Long connectedUserId = null;

    // Authentication methods
    public LoginResponse login(LoginRequest loginRequest) {
        User user = findByEmail(loginRequest.getEmail());

        if (user == null) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Email not found")
                    .build();
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Invalid password")
                    .build();
        }

        // Set connected user
        connectedUserId = user.getId();
        user.setConnected(true);

        return LoginResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .success(true)
                .message("Login successful")
                .build();
    }

    @Transactional
    public LoginResponse register(User user) {
        User existingUser = findByEmail(user.getEmail());

        if (existingUser != null) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Email already exists")
                    .build();
        }

        User createdUser = userRepository.save(user);
        log.info("User saved to database: {}", createdUser.getEmail());

        // Send user to RabbitMQ queues
        UserDTO userDTO = convertToDTO(createdUser);
        userProducer.sendUserToTicketService(userDTO);
        userProducer.sendUserToFeedbackService(userDTO);

        return LoginResponse.builder()
                .userId(createdUser.getId())
                .email(createdUser.getEmail())
                .firstName(createdUser.getFirstName())
                .lastName(createdUser.getLastName())
                .role(createdUser.getRole())
                .success(true)
                .message("Registration successful")
                .build();
    }

    public LoginResponse logout() {
        connectedUserId = null;
        return LoginResponse.builder()
                .success(true)
                .message("Logout successful")
                .build();
    }

    // Get connected user
    public User getConnectedUser() {
        if (connectedUserId == null) {
            return null;
        }
        User user = findById(connectedUserId);
        if (user != null) {
            user.setConnected(true);
        }
        return user;
    }

    public boolean isUserConnected() {
        return connectedUserId != null;
    }

    // User CRUD methods
    public User findByEmail(String email) {
        Optional<User> oUser = userRepository.findByEmail(email);
        return oUser.orElse(null);
    }

    public User findById(Long id) {
        Optional<User> oUser = userRepository.findById(id);
        return oUser.orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = findById(id);
        if (user != null) {
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setRole(userDetails.getRole());
            user.setDatebirth(userDetails.getDatebirth());
            user.setStreet(userDetails.getStreet());
            user.setCity(userDetails.getCity());
            user.setState(userDetails.getState());
            user.setZip(userDetails.getZip());
            user.setPhones(userDetails.getPhones());

            User updatedUser = userRepository.save(user);
            log.info("User updated in database: {}", updatedUser.getEmail());

            // Send updated user to RabbitMQ queues
            UserDTO userDTO = convertToDTO(updatedUser);
            userProducer.sendUserToTicketService(userDTO);
            userProducer.sendUserToFeedbackService(userDTO);

            return updatedUser;
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////
    public List<TicketDTO> getUserTickets(Long userId) {
        User user = findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Appeler le microservice Ticket pour récupérer TOUS les tickets
        List<TicketDTO> allTickets = ticketClient.getAllTickets();

        // Filtrer pour ne garder que les tickets achetés par cet utilisateur
        return allTickets.stream()
                .filter(ticket -> user.getPurchasedTickets().contains(ticket.getId()))
                .collect(java.util.stream.Collectors.toList());
    }

    // Purchase ticket for connected user
    public TicketDTO purchaseTicket(Long ticketId) {
        // Vérifier qu'un utilisateur est connecté
        if (connectedUserId == null) {
            throw new RuntimeException("No user connected. Please login first.");
        }

        User connectedUser = findById(connectedUserId);
        if (connectedUser == null) {
            throw new RuntimeException("Connected user not found");
        }

        // Récupérer tous les tickets via le TicketClient
        List<TicketDTO> allTickets = ticketClient.getAllTickets();

        // Trouver le ticket demandé
        TicketDTO ticket = allTickets.stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        // Vérifier que le ticket est disponible
        if (!"AVAILABLE".equalsIgnoreCase(ticket.getStatus())) {
            throw new RuntimeException("Ticket is not available for purchase. Current status: " + ticket.getStatus());
        }

        // Vérifier si l'utilisateur a déjà acheté ce ticket
        if (connectedUser.getPurchasedTickets().contains(ticketId)) {
            throw new RuntimeException("You have already purchased this ticket");
        }

        // Ajouter le ticket aux tickets achetés de l'utilisateur
        connectedUser.getPurchasedTickets().add(ticketId);
        userRepository.save(connectedUser);

        log.info("Ticket {} purchased by user {} ({})", ticketId, connectedUser.getEmail(), connectedUser.getId());

        return ticket;
    }



    // Save favorite event
    public void saveFavoriteEvent(Long eventId) {
        // Vérifier qu'un utilisateur est connecté
        if (connectedUserId == null) {
            throw new RuntimeException("User must be logged in to save favorite events");
        }

        User user = findById(connectedUserId);
        if (user == null) {
            throw new RuntimeException("Connected user not found");
        }

        // Vérifier si l'événement est déjà dans les favoris
        if (user.getFavoriteEvents().contains(eventId)) {
            throw new RuntimeException("Event is already saved in favorites");
        }

        user.getFavoriteEvents().add(eventId);
        userRepository.save(user);
    }

    public List<EventDTO> getUserFavoriteEvents() {
        // Vérifier qu'un utilisateur est connecté
        if (connectedUserId == null) {
            throw new RuntimeException("User must be logged in to view favorite events");
        }

        User user = findById(connectedUserId);
        if (user == null) {
            throw new RuntimeException("Connected user not found");
        }

        System.out.println("Favorite IDs in DB: " + user.getFavoriteEvents());

        List<EventDTO> allEvents = eventClient.getAllEvents();
        System.out.println("All events from Event service: " + allEvents.size());
        allEvents.forEach(e -> System.out.println("Event ID: " + e.getId()));

        return allEvents.stream()
                .filter(e -> user.getFavoriteEvents().contains(e.getId()))
                .collect(Collectors.toList());
    }

    // Remove favorite event
    public void removeFavoriteEvent(Long eventId) {
        // Vérifier qu'un utilisateur est connecté
        if (connectedUserId == null) {
            throw new RuntimeException("User must be logged in to remove favorite events");
        }

        User user = findById(connectedUserId);
        if (user == null) {
            throw new RuntimeException("Connected user not found");
        }

        // Vérifier si l'événement existe dans les favoris
        if (!user.getFavoriteEvents().contains(eventId)) {
            throw new RuntimeException("Event does not exist in favorites");
        }

        user.getFavoriteEvents().remove(eventId);
        userRepository.save(user);
    }
    /////////////////////////////////////////////////////////

    // Helper method to convert User to UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getDatebirth(),
                user.getStreet(),
                user.getCity(),
                user.getState(),
                user.getZip(),
                user.getPhones()
        );
    }
}