package com.eventplatform.ticket.application.client;

import com.eventplatform.ticket.application.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "eventproject")
public interface EventClient {
    
    @GetMapping("/api/events/{id}")
    EventDTO getEventById(@PathVariable Long id);
}
