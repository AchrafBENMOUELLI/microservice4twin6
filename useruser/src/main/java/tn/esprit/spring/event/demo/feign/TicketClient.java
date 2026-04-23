package tn.esprit.spring.event.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.event.demo.Model.TicketDTO;
import java.util.List;

@FeignClient(name = "ticketproject")
public interface TicketClient {
    @RequestMapping("/api/tickets")
    public List<TicketDTO> getAllTickets();
}