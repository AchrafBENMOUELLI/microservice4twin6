package com.esprit.microservice.feedbackproject;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "Event", configuration = FeignConfig.class)
public interface UserClient {
  @RequestMapping("/api/users/connected")
  public UserDTO getConnectedUser();
}
