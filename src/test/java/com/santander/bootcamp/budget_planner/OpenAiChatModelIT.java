package com.santander.bootcamp.budget_planner;

import org.springframework.ai.chat.model.ChatModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {
    @Autowired
    private ChatModel chatModel;

        @Test
        void shouldReturnNonEmptyResponse() {

            String response = chatModel.call("""
            Responda exatamente com a palavra: Olá
            """);

            assertNotNull(response);
            assertFalse(response.isBlank());

            System.out.println(response);
        }
    }


