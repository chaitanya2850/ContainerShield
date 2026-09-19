package com.example.containershield.controller;

import com.example.containershield.dto.ChatRequest;
import com.example.containershield.dto.ChatResponse;
import com.example.containershield.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String answer = chatService.answerQuestion(
                request.getProjectOrImageName(),
                request.getQuestion()
        );
        return ResponseEntity.ok(new ChatResponse(answer));
    }
}