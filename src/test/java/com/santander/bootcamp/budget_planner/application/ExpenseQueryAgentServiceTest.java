package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseQueryInterpreter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseQueryAgentServiceTest {

    @Mock
    private TranscriptionService transcriptionService;

    @Mock
    private ExpenseQueryInterpreter expenseQueryInterpreter;

    @Mock
    private TextToSpeechService textToSpeechService;

    @Mock
    private ExpenseQueryExecutionRecorder executionRecorder;

    @InjectMocks
    private ExpenseQueryAgentService expenseQueryAgentService;

    @Test
    void shouldTranscribeQuestionAnswerAndSynthesizeAudio() {
        byte[] audioContent = new byte[]{1, 2, 3};
        String transcription = "Quanto gastei em restaurante em julho?";
        String answerText = "Você gastou 45 reais em restaurante em julho de 2026.";

        when(transcriptionService.transcribeExpenseAudio(audioContent, "recording.m4a"))
                .thenReturn(transcription);
        when(expenseQueryInterpreter.answer(transcription)).thenReturn(answerText);
        when(textToSpeechService.synthesizeSpeech(answerText)).thenReturn(new byte[]{9, 8, 7});

        ExpenseQueryAgentResult result = expenseQueryAgentService.queryFromAudio(audioContent, "recording.m4a");

        assertThat(result.transcription()).isEqualTo(transcription);
        assertThat(result.answerText()).isEqualTo(answerText);
        assertThat(result.answerAudio()).containsExactly(9, 8, 7);

        verify(expenseQueryInterpreter).answer(transcription);
        verify(textToSpeechService).synthesizeSpeech(answerText);
    }

    @Test
    void shouldReturnRichQueryResultFromText() {
        String question = "Quanto gastei em restaurante em julho?";
        String answerText = "Você gastou 45 reais em restaurante em julho de 2026.";
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço"
        );
        ExpenseQueryResult queryResult = new ExpenseQueryResult(
                2026,
                7,
                ExpenseCategory.RESTAURANT,
                List.of(expense),
                new BigDecimal("45.90")
        );

        when(expenseQueryInterpreter.answer(question)).thenReturn(answerText);
        when(executionRecorder.getExecutions()).thenReturn(List.of(queryResult));

        ExpenseQueryTextResult result = expenseQueryAgentService.queryFromText(question);

        assertThat(result.question()).isEqualTo(question);
        assertThat(result.answerText()).isEqualTo(answerText);
        assertThat(result.queries()).containsExactly(queryResult);

        verify(expenseQueryInterpreter).answer(question);
    }
}
