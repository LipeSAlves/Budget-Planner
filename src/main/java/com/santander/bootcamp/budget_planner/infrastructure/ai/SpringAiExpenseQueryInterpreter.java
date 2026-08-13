package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryExecutionRecorder;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseQueryInterpreter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class SpringAiExpenseQueryInterpreter implements ExpenseQueryInterpreter {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    private final ChatClient chatClient;
    private final ExpenseAiTools expenseAiTools;
    private final ExpenseQueryExecutionRecorder executionRecorder;

    public SpringAiExpenseQueryInterpreter(
            ChatModel chatModel,
            ExpenseAiTools expenseAiTools,
            ExpenseQueryExecutionRecorder executionRecorder
    ) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.expenseAiTools = expenseAiTools;
        this.executionRecorder = executionRecorder;
    }

    @Override
    public String answer(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("question must not be blank");
        }

        executionRecorder.clear();

        String response = chatClient.prompt()
                .system(buildSystemPrompt())
                .user(question.strip())
                .tools(expenseAiTools)
                .call()
                .content();

        if (response == null || response.isBlank()) {
            throw new IllegalArgumentException("unable to generate expense query answer");
        }

        return response.strip();
    }

    private String buildSystemPrompt() {
        LocalDate referenceDate = LocalDate.now(BRAZIL_ZONE);
        String categories = ExpenseCategory.joinedNames();

        return """
                You are a personal expense assistant for Brazilian Portuguese users.
                Reference date for relative expressions ('hoje', 'ontem', 'este mês', 'julho'): %s

                When the user asks about spending, call listExpenses with the correct year, month, and optional category.
                Valid categories: %s
                If you believe the user is trying to register an expense instead, say the following: 'Me desculpe se estiver interpretando errado, mas você está tentando registrar uma despesa? Se sim, use o canal apropriado para registros; esse canal é destinado apenas para consultas. Caso contrário, poderia refazer sua consulta?'

                Rules:
                - If the year is not mentioned, use reference year instead.
                - If the month is not mentioned, use reference month instead.
                - Respond only in Brazilian Portuguese in 1 or 2 short sentences suitable for text-to-speech.
                - Do not mention JSON, tools, or internal APIs.
                - Do not offer further services (e.g. 'would you like me to...'), answer objectively.
                """.formatted(referenceDate, categories);
    }
}
