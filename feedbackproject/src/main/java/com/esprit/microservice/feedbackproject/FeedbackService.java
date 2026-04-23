package com.esprit.microservice.feedbackproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService implements IFeedbackService {

  @Autowired
  FeedbackJPARepository feedbackJPARepository;

  @Autowired
  UserClient userClient;
  @Autowired
  BlogPostClient blogPostClient;

  @Override
  public List<feedback> getAllFeedbacks() {
    return feedbackJPARepository.findAll();
  }

  @Override
  public feedback getFeedbackById(int id) {
    return feedbackJPARepository.findById(id).orElse(null);
  }


  @Override
  public feedback addFeedback(feedback f) {
      // Get the connected user
      UserDTO user = userClient.getConnectedUser();

      if (user == null) {
          throw new RuntimeException("No connected user found.");
      }

      f.setAuthor(user.getFirstName());

      // Validate blog post only if idblog is provided
      if (f.getIdblog() != null) {
          try {
              Object blog = blogPostClient.getBlogPostById(f.getIdblog());
              if (blog == null) {
                  throw new RuntimeException("Blog post not found with id: " + f.getIdblog());
              }
          } catch (feign.FeignException.NotFound e) {
              throw new RuntimeException("Blog post not found with id: " + f.getIdblog());
          } catch (feign.FeignException e) {
              throw new RuntimeException("Blog service error: " + e.getMessage());
          }
      }

      return feedbackJPARepository.save(f);
  }



  @Override
  public feedback updateFeedback(feedback f) {
    return feedbackJPARepository.save(f);
  }

  @Override
  public void deleteFeedback(int id) {
    feedbackJPARepository.deleteById(id);
  }

  @Override
  public List<feedback> searchBySubject(String subject) {
    return feedbackJPARepository.findBySubjectContainingIgnoreCase(subject);
  }
}
