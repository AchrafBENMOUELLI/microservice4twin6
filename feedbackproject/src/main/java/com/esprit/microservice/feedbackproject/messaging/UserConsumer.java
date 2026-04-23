package com.esprit.microservice.feedbackproject.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.esprit.microservice.feedbackproject.config.RabbitMQConfig;
import com.esprit.microservice.feedbackproject.dto.UserDTO;

@Service
public class UserConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.USER_FEEDBACK_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void receiveUser(UserDTO userDTO) {
        log.info("========== FEEDBACK SERVICE: UserDTO RECEIVED ==========");
        log.info("User ID: {}", userDTO.getId());
        log.info("User Email: {}", userDTO.getEmail());
        log.info("User Name: {} {}", userDTO.getFirstName(), userDTO.getLastName());
        log.info("User Role: {}", userDTO.getRole());
        log.info("========================================================");
        
        // Logique métier : stocker en cache pour les feedbacks
        try {
            // Add your business logic here
            log.info("User successfully processed in Feedback service");
        } catch (Exception e) {
            log.error("Error processing user in Feedback service: {}", e.getMessage(), e);
            throw e;
        }
    }
}
