package com.esprit.microservice.apigatewaydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayDemoApplication.class, args);
  }

  @Bean
  public RouteLocator gatewayroutes(RouteLocatorBuilder builder) {

    return builder.routes()
            // User Service routes (NEW back_user1)
            .route("user-service", r -> r.path("/api/users/**", "/api/auth/**")
                    .uri("lb://Event"))
            
            // Event Service routes
            .route("event-service", r -> r.path("/api/events/**")
                    .uri("lb://eventproject"))
            
            // Ticket Service routes
            .route("ticket-service", r -> r.path("/api/tickets/**")
                    .uri("lb://ticketproject"))
            
            // Blog Service routes
            .route("blog-service", r -> r.path("/api/blog-posts/**")
                    .uri("lb://blog-module"))
            
            // Feedback Service routes (NEW)
            .route("feedback-service", r -> r.path("/feedback/**")
                    .uri("lb://feedbackproject"))
            
            // Candidate Service routes (existing)
            .route("candidate-service", r -> r.path("/candidats/**")
                    .uri("lb://MSCANDIDAT4TWIN6"))
            
            .build();
  }

}
