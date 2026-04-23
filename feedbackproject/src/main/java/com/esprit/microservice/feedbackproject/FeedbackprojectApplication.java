package com.esprit.microservice.feedbackproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class FeedbackprojectApplication {

  public static void main(String[] args) {
    SpringApplication.run(FeedbackprojectApplication.class, args);
  }

  @Autowired
  private FeedbackJPARepository repository;

  @Bean
  ApplicationRunner init() {
    return (args) -> {
      // save
      repository.save(new feedback("Great Service", "I really enjoyed the experience!", 5, "Ali Ben Salah"));
      repository.save(new feedback("Good Job", "The team was very helpful.", 4, "Sarra Mansour"));
      repository.save(new feedback("Average", "It was okay, could be better.", 3, "Mohamed Trabelsi"));
      repository.save(new feedback("Not Satisfied", "The response time was too slow.", 2, "Maroua Dhiab"));
      repository.save(new feedback("Terrible", "Very disappointing experience.", 1, "Achraf Ben Mouelli"));

      // fetch
      repository.findAll().forEach(System.out::println);
    };
  }
}
