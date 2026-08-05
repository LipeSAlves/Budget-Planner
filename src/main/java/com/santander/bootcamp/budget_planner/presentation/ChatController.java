package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.ChatPromptService;
import com.santander.bootcamp.budget_planner.presentation.dto.ChatPromptRequest;
import com.santander.bootcamp.budget_planner.presentation.dto.ChatPromptResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatPromptService chatPromptService;

    public ChatController(ChatPromptService chatPromptService) {
        this.chatPromptService = chatPromptService;
    }

    @PostMapping
    public ResponseEntity<ChatPromptResponse> chat(@Valid @RequestBody ChatPromptRequest request) {
        String response = chatPromptService.handlePrompt(request.prompt());

        return ResponseEntity.ok(new ChatPromptResponse(response));
    }
}
