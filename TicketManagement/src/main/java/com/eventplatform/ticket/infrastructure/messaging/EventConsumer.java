package com.eventplatform.ticket.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.eventplatform.ticket.application.dto.EventDTO;
import com.eventplatform.ticket.infrastructure.config.RabbitMQConfig;

@Service
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.EVENT_TICKET_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveEvent(EventDTO eventDTO) {
        log.info("========== TICKET SERVICE: EventDTO RECEIVED ==========");
        log.info("Event ID: {}", eventDTO.getId());
        log.info("Event Title: {}", eventDTO.getTitle());
        log.info("Event Date: {}", eventDTO.getDate());
        log.info("Event Location: {}", eventDTO.getLocation());
        log.info("Event Price: {}", eventDTO.getPrice());
        log.info("Event Organizer ID: {}", eventDTO.getOrganizerid());
        log.info("Event Places: {}", eventDTO.getNbplaces());
        log.info("=======================================================");
        
        // Logique métier : stocker en cache, notifier, etc.
        try {
            // Add your business logic here
            log.info("Event successfully processed in Ticket service");
        } catch (Exception e) {
            log.error("Error processing event in Ticket service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
