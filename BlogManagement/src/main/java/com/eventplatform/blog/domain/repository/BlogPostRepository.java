package com.eventplatform.blog.domain.repository;

import com.eventplatform.blog.domain.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByPublishedTrue();
    List<BlogPost> findByUserId(Long userId);
    List<BlogPost> findByEventId(Long eventId);
}
