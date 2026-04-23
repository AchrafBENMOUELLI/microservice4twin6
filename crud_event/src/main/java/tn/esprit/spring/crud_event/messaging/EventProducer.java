package tn.esprit.spring.crud_event.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.crud_event.config.RabbitMQConfig;
import tn.esprit.spring.crud_event.dto.EventDTO;

@Service
public class EventProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);

    public EventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEventToTicketService(EventDTO eventDTO) {
        try {
            log.info("========== EVENT SERVICE: Sending Event to TICKET Queue ==========");
            log.info("Event ID: {}, Title: {}, Date: {}", eventDTO.getId(), eventDTO.getTitle(), eventDTO.getDate());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EVENT_TICKET_QUEUE, eventDTO);
            log.info("Event successfully sent to Ticket queue: {}", eventDTO.getTitle());
            log.info("===================================================================");
        } catch (AmqpException e) {
            log.error("ERROR sending event to RabbitMQ (Ticket queue): {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sendEventToBlogService(EventDTO eventDTO) {
        try {
            log.info("========== EVENT SERVICE: Sending Event to BLOG Queue ==========");
            log.info("Event ID: {}, Title: {}, Date: {}", eventDTO.getId(), eventDTO.getTitle(), eventDTO.getDate());
            rabbitTemplate.convertAndSend(RabbitMQConfig.EVENT_BLOG_QUEUE, eventDTO);
            log.info("Event successfully sent to Blog queue: {}", eventDTO.getTitle());
            log.info("================================================================");
        } catch (AmqpException e) {
            log.error("ERROR sending event to RabbitMQ (Blog queue): {}", e.getMessage(), e);
            throw e;
        }
    }
}
