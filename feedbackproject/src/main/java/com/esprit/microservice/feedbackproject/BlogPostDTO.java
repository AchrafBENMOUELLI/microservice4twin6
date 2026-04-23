package com.esprit.microservice.feedbackproject;

import java.time.LocalDateTime;

public class BlogPostDTO {

  private Long id;
  private String title;
  private String content;
  private String author;
  private Boolean published;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // No-args constructor
  public BlogPostDTO() {}

  // All-args constructor
  public BlogPostDTO(Long id, String title, String content, String author,
                     Boolean published, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.author = author;
    this.published = published;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Getters & Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }

  public String getAuthor() { return author; }
  public void setAuthor(String author) { this.author = author; }

  public Boolean getPublished() { return published; }
  public void setPublished(Boolean published) { this.published = published; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
