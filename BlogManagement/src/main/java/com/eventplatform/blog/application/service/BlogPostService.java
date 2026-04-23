package com.eventplatform.blog.application.service;

import com.eventplatform.blog.application.dto.BlogPostRequest;
import com.eventplatform.blog.application.dto.BlogPostResponse;
import com.eventplatform.blog.application.dto.EventDTO;
import com.eventplatform.blog.domain.entity.BlogPost;
import com.eventplatform.blog.domain.repository.BlogPostRepository;
import com.eventplatform.blog.infrastructure.exception.EventNotFoundException;
import com.eventplatform.blog.infrastructure.exception.ResourceNotFoundException;
import com.eventplatform.blog.infrastructure.exception.UnauthorizedException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final EventClient eventClient;

    @Transactional
    public BlogPostResponse createBlogPost(BlogPostRequest request) {
        // Get user ID from request body
        Long userId = request.getUserId();

        // Validate that the event exists
        EventDTO event = validateEventExists(request.getEventId());

        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setUserId(userId);
        blogPost.setEventId(request.getEventId());
        blogPost.setPublished(request.getPublished());

        BlogPost savedPost = blogPostRepository.save(blogPost);
        return mapToResponse(savedPost, event);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
        return mapToResponseWithEvent(blogPost);
    }

    @Transactional(readOnly = true)
    public List<BlogPostResponse> getAllBlogPosts() {
        return blogPostRepository.findAll().stream()
                .map(this::mapToResponseWithEvent)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogPostResponse> getPublishedBlogPosts() {
        return blogPostRepository.findByPublishedTrue().stream()
                .map(this::mapToResponseWithEvent)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogPostResponse> getBlogPostsByUserId(Long userId) {
        return blogPostRepository.findByUserId(userId).stream()
                .map(this::mapToResponseWithEvent)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BlogPostResponse> getBlogPostsByEventId(Long eventId) {
        return blogPostRepository.findByEventId(eventId).stream()
                .map(this::mapToResponseWithEvent)
                .collect(Collectors.toList());
    }

    @Transactional
    public BlogPostResponse updateBlogPost(Long id, BlogPostRequest request) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        // Check ownership
        if (!blogPost.getUserId().equals(request.getUserId())) {
            throw new UnauthorizedException("You are not authorized to update this blog post");
        }

        // Validate that the event exists if eventId is being changed
        EventDTO event = null;
        if (!blogPost.getEventId().equals(request.getEventId())) {
            event = validateEventExists(request.getEventId());
        }

        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setEventId(request.getEventId());
        blogPost.setPublished(request.getPublished());

        BlogPost updatedPost = blogPostRepository.save(blogPost);

        // Fetch event if not already fetched
        if (event == null) {
            event = getEventById(updatedPost.getEventId());
        }

        return mapToResponse(updatedPost, event);
    }

    @Transactional
    public void deleteBlogPost(Long id, Long userId) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        // Check ownership
        if (!blogPost.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this blog post");
        }

        blogPostRepository.deleteById(id);
    }

    /**
     * Validate that an event exists by calling the Event service
     * @param eventId the event ID to validate
     * @return the EventDTO if found
     * @throws EventNotFoundException if the event doesn't exist
     */
    private EventDTO validateEventExists(Long eventId) {
        try {
            return eventClient.getEventById(eventId);
        } catch (FeignException.NotFound e) {
            throw new EventNotFoundException(eventId);
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with Event service: " + e.getMessage());
        }
    }

    /**
     * Get event by ID from Event service
     * @param eventId the event ID
     * @return the EventDTO or null if not found
     */
    private EventDTO getEventById(Long eventId) {
        try {
            return eventClient.getEventById(eventId);
        } catch (FeignException e) {
            // Return null if event not found or service unavailable
            return null;
        }
    }

    /**
     * Map BlogPost to response with event data fetched from Event service
     */
    private BlogPostResponse mapToResponseWithEvent(BlogPost blogPost) {
        EventDTO event = getEventById(blogPost.getEventId());
        return mapToResponse(blogPost, event);
    }

    /**
     * Map BlogPost to response with provided event data
     */
    private BlogPostResponse mapToResponse(BlogPost blogPost, EventDTO event) {
        return new BlogPostResponse(
                blogPost.getId(),
                blogPost.getTitle(),
                blogPost.getContent(),
                blogPost.getUserId(),
                blogPost.getEventId(),
                event,
                blogPost.getPublished(),
                blogPost.getCreatedAt(),
                blogPost.getUpdatedAt()
        );
    }

    public EventDTO getEvent(Long eventId) {
        return eventClient.getEventById(eventId);
    }
}
