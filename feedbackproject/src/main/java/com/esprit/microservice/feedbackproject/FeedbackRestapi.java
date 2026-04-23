package com.esprit.microservice.feedbackproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackRestapi {

  @Autowired
  FeedbackService feedbackService;

  @GetMapping
  public List<feedback> getAllFeedbacks() {
    return feedbackService.getAllFeedbacks();
  }

  @GetMapping("/{id}")
  public feedback getFeedbackById(@PathVariable int id) {
    return feedbackService.getFeedbackById(id);
  }

  @PostMapping
  public feedback addFeedback(@RequestBody feedback f) {
    return feedbackService.addFeedback(f);
  }

  // ✅ AJOUT — user peut ajouter un feedback
  @PostMapping("/user/add")
  public ResponseEntity<?> addFeedbackUser(@RequestBody feedback f) {
    try {
      feedback saved = feedbackService.addFeedback(f);
      return ResponseEntity.ok(saved);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public feedback updateFeedback(@PathVariable int id, @RequestBody feedback f) {
    f.setId(id);
    return feedbackService.updateFeedback(f);
  }

  @DeleteMapping("/{id}")
  public void deleteFeedback(@PathVariable int id) {
    feedbackService.deleteFeedback(id);
  }

  // ✅ AJOUT — admin peut supprimer un feedback
  @DeleteMapping("/admin/{id}")
  public void deleteFeedbackAdmin(@PathVariable int id) {
    feedbackService.deleteFeedback(id);
  }

  @Value("${welcome.message}")
  private String welcomeMessage;

  @GetMapping("/welcome")
  public String welcome() {
    return welcomeMessage;
  }

  @GetMapping("/search")
  public List<feedback> searchBySubject(@RequestParam String subject) {
    return feedbackService.searchBySubject(subject);
  }
}
