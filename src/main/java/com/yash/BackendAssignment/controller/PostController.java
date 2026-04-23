package com.yash.BackendAssignment.controller;

import com.yash.BackendAssignment.entity.Post;
import com.yash.BackendAssignment.repos.PostRepository;
import com.yash.BackendAssignment.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;


    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postRepository.save(post);
    }


    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }


    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    @Autowired
    private RedisService redisService;

    @PostMapping("/{postId}/like")
    public String likePost(@PathVariable Long postId) {
        redisService.incrementScore(postId, 20);
        return "Post liked";
    }
}
