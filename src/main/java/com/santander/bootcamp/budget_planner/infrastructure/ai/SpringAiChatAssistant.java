package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.domain.port.ChatAssistant;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class SpringAiChatAssistant implements ChatAssistant {

    private final ChatModel chatModel;

    public SpringAiChatAssistant(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String ask(String prompt) {
        return chatModel.call(prompt);
    }
}
