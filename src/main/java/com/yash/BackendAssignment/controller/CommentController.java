package com.yash.BackendAssignment.controller;

import com.yash.BackendAssignment.entity.Comment;
import com.yash.BackendAssignment.entity.AuthorType;
import com.yash.BackendAssignment.repos.BotRepository;
import com.yash.BackendAssignment.repos.CommentRepository;
import com.yash.BackendAssignment.repos.UserRepository;
import com.yash.BackendAssignment.service.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BotRepository botRepository;


    @Autowired
    private RedisService redisService;

    @PostMapping("/{postId}/comments")
    public Comment addComment(@PathVariable Long postId,
                              @RequestBody Comment comment) {


        if (comment.getAuthorType() == AuthorType.USER) {
            userRepository.findById(comment.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else if (comment.getAuthorType() == AuthorType.BOT) {
            botRepository.findById(comment.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Bot not found"));
        }


        int depth;

        if (comment.getParentCommentId() == null) {
            depth = 1;
        } else {
            Comment parent = commentRepository.findById(comment.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));

            if (!parent.getPostId().equals(postId)) {
                throw new RuntimeException("Parent belongs to different post");
            }

            depth = parent.getDepthLevel() + 1;
        }


        if (depth > 20) {
            throw new RuntimeException("Depth limit exceeded");
        }

        comment.setPostId(postId);
        comment.setDepthLevel(depth);


        if (comment.getAuthorType() == AuthorType.USER) {
            redisService.incrementScore(postId, 50);
        } else if (comment.getAuthorType() == AuthorType.BOT) {
            redisService.incrementScore(postId, 1);
        }

        return commentRepository.save(comment);
    }
}