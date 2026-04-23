package tn.esprit.spring.event.demo.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.event.config.RabbitMQConfig;
import tn.esprit.spring.event.demo.dto.UserDTO;

@Service
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(UserProducer.class);

    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserToTicketService(UserDTO userDTO) {
        try {
            log.info("========== USER SERVICE: Sending User to TICKET Queue ==========");
            log.info("User ID: {}, Email: {}, Name: {} {}", userDTO.getId(), userDTO.getEmail(), 
                     userDTO.getFirstName(), userDTO.getLastName());
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_TICKET_QUEUE, userDTO);
            log.info("User successfully sent to Ticket queue: {}", userDTO.getEmail());
            log.info("================================================================");
        } catch (AmqpException e) {
            log.error("ERROR sending user to RabbitMQ (Ticket queue): {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sendUserToFeedbackService(UserDTO userDTO) {
        try {
            log.info("========== USER SERVICE: Sending User to FEEDBACK Queue ==========");
            log.info("User ID: {}, Email: {}, Name: {} {}", userDTO.getId(), userDTO.getEmail(), 
                     userDTO.getFirstName(), userDTO.getLastName());
            rabbitTemplate.convertAndSend(RabbitMQConfig.USER_FEEDBACK_QUEUE, userDTO);
            log.info("User successfully sent to Feedback queue: {}", userDTO.getEmail());
            log.info("===================================================================");
        } catch (AmqpException e) {
            log.error("ERROR sending user to RabbitMQ (Feedback queue): {}", e.getMessage(), e);
            throw e;
        }
    }
}
