package com.eventplatform.blog.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.eventplatform.blog.application.dto.EventDTO;
import com.eventplatform.blog.infrastructure.config.RabbitMQConfig;

@Service
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.EVENT_BLOG_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveEvent(EventDTO eventDTO) {
        log.info("========== BLOG SERVICE: EventDTO RECEIVED ==========");
        log.info("Event ID: {}", eventDTO.getId());
        log.info("Event Title: {}", eventDTO.getTitle());
        log.info("Event Date: {}", eventDTO.getDate());
        log.info("Event Location: {}", eventDTO.getLocation());
        log.info("Event Price: {}", eventDTO.getPrice());
        log.info("Event Organizer ID: {}", eventDTO.getOrganizerid());
        log.info("Event Places: {}", eventDTO.getNbplaces());
        log.info("=====================================================");
        
        // Logique métier : stocker en cache pour les blog posts
        try {
            // Add your business logic here
            log.info("Event successfully processed in Blog service");
        } catch (Exception e) {
            log.error("Error processing event in Blog service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
