package com.esprit.microservice.feedbackproject;

import java.util.List;

public interface IFeedbackService {
  List<feedback> getAllFeedbacks();
  feedback getFeedbackById(int id);
  feedback addFeedback(feedback f);
  feedback updateFeedback(feedback f);
  void deleteFeedback(int id);
  List<feedback> searchBySubject(String subject);
}
