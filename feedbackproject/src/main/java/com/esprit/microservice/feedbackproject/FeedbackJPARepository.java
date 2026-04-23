package com.esprit.microservice.feedbackproject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackJPARepository extends JpaRepository<feedback, Integer> {
  List<feedback> findBySubjectContainingIgnoreCase(String subject);
}
