package com.eventplatform.blog.presentation.controller;

import com.eventplatform.blog.application.dto.BlogPostRequest;
import com.eventplatform.blog.application.dto.BlogPostResponse;
import com.eventplatform.blog.application.dto.EventDTO;
import com.eventplatform.blog.application.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogPostController {

  private final BlogPostService blogPostService;

  // ─── USER endpoints (role: user) ────────────────────────────

  @PostMapping("/api/blog-posts/user/add")
  public ResponseEntity<BlogPostResponse> createBlogPost(
    @Valid @RequestBody BlogPostRequest request) {
    BlogPostResponse response = blogPostService.createBlogPost(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/api/blog-posts/user/{id}")
  public ResponseEntity<BlogPostResponse> getBlogPostById(@PathVariable Long id) {
    return ResponseEntity.ok(blogPostService.getBlogPostById(id));
  }

  @GetMapping("/api/blog-posts/user/all")
  public ResponseEntity<List<BlogPostResponse>> getAllBlogPosts(
    @RequestParam(required = false) Boolean published,
    @RequestParam(required = false) Long userId,
    @RequestParam(required = false) Long eventId) {

    List<BlogPostResponse> responses;
    if (published != null && published) {
      responses = blogPostService.getPublishedBlogPosts();
    } else if (userId != null) {
      responses = blogPostService.getBlogPostsByUserId(userId);
    } else if (eventId != null) {
      responses = blogPostService.getBlogPostsByEventId(eventId);
    } else {
      responses = blogPostService.getAllBlogPosts();
    }
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/api/blog-posts/user/event/{id}")
  public EventDTO getEventFromEventService(@PathVariable Long id) {
    return blogPostService.getEvent(id);
  }

  @PutMapping("/api/blog-posts/user/update/{id}")
  public ResponseEntity<BlogPostResponse> updateBlogPost(
    @PathVariable Long id,
    @Valid @RequestBody BlogPostRequest request) {
    return ResponseEntity.ok(blogPostService.updateBlogPost(id, request));
  }

  // ─── ADMIN endpoints (role: admin) ──────────────────────────

  @DeleteMapping("/api/blog-posts/admin/delete/{id}")
  public ResponseEntity<Void> deleteBlogPost(
    @PathVariable Long id,
    @RequestParam Long userId) {
    blogPostService.deleteBlogPost(id, userId);
    return ResponseEntity.noContent().build();
  }

  // ─── PUBLIC (authenticated only) ────────────────────────────

  @Value("${welcome.message}")
  private String welcomeMessage;

  @GetMapping("/api/blog-posts/welcome")
  public String welcome() {
    return welcomeMessage;
  }
}
