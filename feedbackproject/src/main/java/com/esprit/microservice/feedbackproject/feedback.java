package com.esprit.microservice.feedbackproject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class feedback {

  @Id
  @GeneratedValue
  private int id;

  private String subject;
  private String message;
  private int rating;
  private Long idblog;
  private String author;

  public feedback(String subject, String message, int rating, String author) {
    this.subject = subject;
    this.message = message;
    this.rating = rating;
    this.author = author;
  }

  public feedback() {}

  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getSubject() { return subject; }
  public void setSubject(String subject) { this.subject = subject; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public int getRating() { return rating; }
  public void setRating(int rating) { this.rating = rating; }

  public Long getIdblog() { return idblog; }
  public void setIdblog(Long idblog) { this.idblog = idblog; }

  public String getAuthor() { return author; }
  public void setAuthor(String author) { this.author = author; }
}
