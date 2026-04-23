package com.eventplatform.blog.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Long eventId;
    private EventDTO event;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
