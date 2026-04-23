package tn.esprit.spring.event.demo.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.event.demo.Model.*;
import tn.esprit.spring.event.demo.Service.UserService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class UserController {

  @Autowired
  private UserService userService;

  // ─── PUBLIC — no token needed ────────────────────────────────

  @PostMapping("/api/auth/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    LoginResponse response = userService.login(loginRequest);
    if (response.isSuccess()) return ResponseEntity.ok(response);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @PostMapping("/api/auth/register")
  public ResponseEntity<LoginResponse> register(@RequestBody User user) {
    LoginResponse response = userService.register(user);
    if (response.isSuccess()) return ResponseEntity.status(HttpStatus.CREATED).body(response);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @PostMapping("/api/auth/logout")
  public ResponseEntity<LoginResponse> logout() {
    return ResponseEntity.ok(userService.logout());
  }

  // ─── USER endpoints (role: user) ────────────────────────────

  @GetMapping("/api/users/all")
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping("/api/users/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    User user = userService.findById(id);
    if (user != null) return ResponseEntity.ok(user);
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/api/users/email/{email}")
  public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
    User user = userService.findByEmail(email);
    if (user != null) return ResponseEntity.ok(user);
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/api/users/connected")
  public ResponseEntity<User> getConnectedUser() {
    User user = userService.getConnectedUser();
    if (user != null) return ResponseEntity.ok(user);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }

  @GetMapping("/api/users/isConnected")
  public ResponseEntity<Boolean> isUserConnected() {
    return ResponseEntity.ok(userService.isUserConnected());
  }

  @PutMapping("/api/users/update/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
    User updatedUser = userService.updateUser(id, user);
    if (updatedUser != null) return ResponseEntity.ok(updatedUser);
    return ResponseEntity.notFound().build();
  }

  @GetMapping("/api/users/my-tickets")
  public ResponseEntity<?> getConnectedUserTickets() {
    try {
      User connectedUser = userService.getConnectedUser();
      if (connectedUser == null)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user connected");
      List<TicketDTO> tickets = userService.getUserTickets(connectedUser.getId());
      return ResponseEntity.ok(tickets);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }

  @GetMapping("/api/users/{userId}/tickets")
  public ResponseEntity<List<TicketDTO>> getUserTickets(@PathVariable Long userId) {
    try {
      return ResponseEntity.ok(userService.getUserTickets(userId));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  @PostMapping("/api/users/buy-ticket/{ticketId}")
  public ResponseEntity<?> purchaseTicket(@PathVariable Long ticketId) {
    try {
      return ResponseEntity.ok(userService.purchaseTicket(ticketId));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @PostMapping("/api/users/favorite-events/{eventId}")
  public ResponseEntity<String> saveFavoriteEvent(@PathVariable Long eventId) {
    try {
      userService.saveFavoriteEvent(eventId);
      return ResponseEntity.ok("Event saved as favorite successfully.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping("/api/users/favorite-events")
  public ResponseEntity<?> getUserFavoriteEvents() {
    try {
      return ResponseEntity.ok(userService.getUserFavoriteEvents());
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
  }

  @DeleteMapping("/api/users/favorite-events/{eventId}")
  public ResponseEntity<String> removeFavoriteEvent(@PathVariable Long eventId) {
    try {
      userService.removeFavoriteEvent(eventId);
      return ResponseEntity.ok("Event removed from favorites successfully.");
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  // ─── ADMIN endpoints (role: admin) ──────────────────────────

  @DeleteMapping("/api/users/admin/delete/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    boolean deleted = userService.deleteUser(id);
    if (deleted) return ResponseEntity.noContent().build();
    return ResponseEntity.notFound().build();
  }
}
