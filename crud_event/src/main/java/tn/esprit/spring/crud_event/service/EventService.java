package tn.esprit.spring.crud_event.service;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.crud_event.client.UserClient;
import tn.esprit.spring.crud_event.dto.EventDTO;
import tn.esprit.spring.crud_event.dto.UserDTO;
import tn.esprit.spring.crud_event.exception.ResourceNotFoundException;
import tn.esprit.spring.crud_event.messaging.EventProducer;
import tn.esprit.spring.crud_event.model.Event;
import tn.esprit.spring.crud_event.repo.EventRepository;

import java.util.List;

@Service
public class EventService {
    private final EventRepository repo;
    private final UserClient userClient;
    private final EventProducer eventProducer;
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public EventService(EventRepository repo, UserClient userClient, EventProducer eventProducer) {
        this.repo = repo;
        this.userClient = userClient;
        this.eventProducer = eventProducer;
    }

    public List<Event> findAll() {
        return repo.findAll();
    }

    public Event findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    public List<Event> findByOrganizerId(Long organizerId) {
        return repo.findByOrganizerid(organizerId);
    }

    @Transactional
    public Event create(Event event) {
        // Validate organizer exists
        if (event.getOrganizerid() != null) {
            try {
                UserDTO user = userClient.getUserById(event.getOrganizerid());
                if (user == null) {
                    throw new RuntimeException("Organizer not found with id: " + event.getOrganizerid());
                }
            } catch (FeignException.NotFound e) {
                throw new RuntimeException("Organizer not found with id: " + event.getOrganizerid());
            } catch (FeignException e) {
                throw new RuntimeException("Error communicating with User service: " + e.getMessage());
            }
        }
        
        Event savedEvent = repo.save(event);
        log.info("Event sauvegardé en base : {}", savedEvent.getTitle());

        // Construire un DTO à envoyer
        EventDTO eventDTO = convertToDTO(savedEvent);

        // Envoi asynchrone via RabbitMQ aux services Ticket et Blog
        eventProducer.sendEventToTicketService(eventDTO);
        eventProducer.sendEventToBlogService(eventDTO);

        return savedEvent;
    }

    @Transactional
    public Event update(Long id, Event updated) {
        Event existing = findById(id);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setDate(updated.getDate());
        existing.setLocation(updated.getLocation());
        existing.setPrice(updated.getPrice());
        existing.setOrganizerid(updated.getOrganizerid());
        existing.setImageUrl(updated.getImageUrl());
        existing.setNbplaces(updated.getNbplaces());
        existing.setNblikes(updated.getNblikes());

        Event updatedEvent = repo.save(existing);
        log.info("Event mis à jour : {}", updatedEvent.getTitle());

        // Envoyer la mise à jour via RabbitMQ
        EventDTO eventDTO = convertToDTO(updatedEvent);
        eventProducer.sendEventToTicketService(eventDTO);
        eventProducer.sendEventToBlogService(eventDTO);

        return updatedEvent;
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private EventDTO convertToDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getLocation(),
                event.getPrice(),
                event.getOrganizerid(),
                event.getImageUrl(),
                event.getNbplaces(),
                event.getNblikes()
        );
    }
}
