package tn.esprit.spring.event.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.event.demo.Model.EventDTO;
import java.util.List;

@FeignClient(name = "eventproject")
public interface EventClient {
    @RequestMapping("/api/events")
    public List<EventDTO> getAllEvents();


}