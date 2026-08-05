package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.port.ChatAssistant;
import org.springframework.stereotype.Service;

@Service
public class ChatPromptService {

    private final ChatAssistant chatAssistant;

    public ChatPromptService(ChatAssistant chatAssistant) {
        this.chatAssistant = chatAssistant;
    }

    public String handlePrompt(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("prompt must not be blank");
        }

        return chatAssistant.ask(prompt.strip());
    }
}
