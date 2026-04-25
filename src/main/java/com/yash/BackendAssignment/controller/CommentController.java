package com.yash.BackendAssignment.controller;

import com.yash.BackendAssignment.entity.Comment;
import com.yash.BackendAssignment.entity.AuthorType;
import com.yash.BackendAssignment.repos.BotRepository;
import com.yash.BackendAssignment.repos.CommentRepository;
import com.yash.BackendAssignment.repos.UserRepository;
import com.yash.BackendAssignment.service.RedisService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        Comment parent = null;

        if (comment.getParentCommentId() == null) {
            depth = 1;
        } else {
            parent = commentRepository.findById(comment.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));

            if (!parent.getPostId().equals(postId)) {
                throw new RuntimeException("Parent belongs to different post");//to check wheter the comment is on the same post
            }

            depth = parent.getDepthLevel() + 1;
        }

        if (depth > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Depth limit exceeded");
        }

        comment.setPostId(postId);
        comment.setDepthLevel(depth);

        if (comment.getAuthorType() == AuthorType.BOT) {

            boolean allowed = redisService.allowBot(postId);

            if (!allowed) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot limit exceeded");
            }

            if (parent != null && parent.getAuthorType() == AuthorType.USER) {

                //cool down b/w user and specific bot

                boolean allowedCooldown = redisService.allowBotToUser(
                        comment.getAuthorId(),
                        parent.getAuthorId()
                );

                if (!allowedCooldown) {
                    throw new ResponseStatusException(
                            HttpStatus.TOO_MANY_REQUESTS,
                            "Cooldown active"
                    );
                }
                //push notif to console through redis
                redisService.handleNotification(
                        parent.getAuthorId(),
                        "Bot " + comment.getAuthorId() + " replied to your comment"
                );
            }
        }

        if (comment.getAuthorType() == AuthorType.USER) {
            redisService.incrementScore(postId, 50);
        } else if (comment.getAuthorType() == AuthorType.BOT) {
            redisService.incrementScore(postId, 1);
        }

        return commentRepository.save(comment);
    }
}