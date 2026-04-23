package tn.esprit.spring.crud_event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.crud_event.model.Event;
import tn.esprit.spring.crud_event.service.EventService;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EventController {

  private final EventService service;

  public EventController(EventService service) {
    this.service = service;
  }

  // ─── USER endpoints (role: user) ────────────────────────────

  @GetMapping("/api/events/user/all")
  public List<Event> all() {
    return service.findAll();
  }

  @GetMapping("/api/events/user/{id}")
  public Event get(@PathVariable Long id) {
    return service.findById(id);
  }

  @GetMapping("/api/events/user/organizer/{organizerId}")
  public List<Event> getByOrganizer(@PathVariable Long organizerId) {
    return service.findByOrganizerId(organizerId);
  }

  @PostMapping("/api/events/user/add")
  public ResponseEntity<?> create(@Valid @RequestBody Event event) {
    try {
      Event saved = service.create(event);
      return ResponseEntity
        .created(URI.create("/api/events/user/" + saved.getId()))
        .body(saved);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  @PutMapping("/api/events/user/update/{id}")
  public Event update(@PathVariable Long id, @Valid @RequestBody Event event) {
    return service.update(id, event);
  }

  // ─── ADMIN endpoints (role: admin) ──────────────────────────

  @DeleteMapping("/api/events/admin/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  // ─── PUBLIC (authenticated only) ────────────────────────────

  @Value("${welcome.message}")
  private String welcomeMessage;

  @GetMapping("/api/events/welcome")
  public String welcome() {
    return welcomeMessage;
  }
}
