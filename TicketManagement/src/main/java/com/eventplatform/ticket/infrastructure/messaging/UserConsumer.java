package com.eventplatform.ticket.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.eventplatform.ticket.application.dto.UserDTO;
import com.eventplatform.ticket.infrastructure.config.RabbitMQConfig;

@Service
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.USER_TICKET_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserDTO userDTO) {
        log.info("========== TICKET SERVICE: UserDTO RECEIVED ==========");
        log.info("User ID: {}", userDTO.getId());
        log.info("User Email: {}", userDTO.getEmail());
        log.info("User Name: {} {}", userDTO.getFirstName(), userDTO.getLastName());
        log.info("User Role: {}", userDTO.getRole());
        log.info("======================================================");
        
        // Logique métier : stocker en cache, notifier, etc.
        try {
            // Add your business logic here
            log.info("User successfully processed in Ticket service");
        } catch (Exception e) {
            log.error("Error processing user in Ticket service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
