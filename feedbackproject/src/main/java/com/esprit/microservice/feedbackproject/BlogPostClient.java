package com.esprit.microservice.feedbackproject;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "blog-module", configuration = FeignConfig.class)
public interface BlogPostClient {

    @GetMapping("/api/blog-posts/{id}")
    Object getBlogPostById(@PathVariable("id") Long id); // change to Object
}
