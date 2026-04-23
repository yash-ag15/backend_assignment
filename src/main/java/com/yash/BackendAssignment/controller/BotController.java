package com.yash.BackendAssignment.controller;

import com.yash.BackendAssignment.entity.Bot;
import com.yash.BackendAssignment.repos.BotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    @Autowired
    private BotRepository botRepository;

    @PostMapping
    public Bot createBot(@RequestBody Bot bot) {
        return botRepository.save(bot);
    }

    @GetMapping("/{id}")
    public Bot getBot(@PathVariable Long id) {
        return botRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bot not found"));
    }

    @GetMapping
    public List<Bot> getAllBots() {
        return botRepository.findAll();
    }
}