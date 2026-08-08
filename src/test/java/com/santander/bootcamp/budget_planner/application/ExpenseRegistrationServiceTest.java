package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseRegistrationServiceTest {

    @Mock
    private TranscriptionService transcriptionService;

    @Mock
    private ExpenseExtractionService expenseExtractionService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private TextToSpeechService textToSpeechService;

    @InjectMocks
    private ExpenseRegistrationService expenseRegistrationService;

    @Test
    void shouldTranscribeExtractAndPersistExpense() {
        byte[] audioContent = new byte[]{1, 2, 3};
        String transcription = "Gastei 45 reais no restaurante";
        ParsedExpense parsedExpense = new ParsedExpense(
                Money.brl(new BigDecimal("45.90")),
                ExpenseCategory.RESTAURANT,
                Instant.parse("2026-08-05T14:00:00Z")
        );

        when(transcriptionService.transcribeExpenseAudio(audioContent, "recording.m4a"))
                .thenReturn(transcription);
        when(expenseExtractionService.extractFromTranscription(transcription))
                .thenReturn(parsedExpense);
        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(textToSpeechService.synthesizeSpeech("Gasto registrado: 45 reais e 90 centavos na categoria restaurante, incluído no mês de agosto."))
                .thenReturn(new byte[]{9, 8, 7});

        ExpenseRegistrationResult result = expenseRegistrationService.registerFromAudio(
                audioContent,
                "recording.m4a"
        );

        assertThat(result.transcription()).isEqualTo(transcription);
        assertThat(result.expense().getCategory()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(result.expense().getMoney().amount()).isEqualByComparingTo("45.90");
        assertThat(result.expense().getOccurredAt()).isEqualTo(Instant.parse("2026-08-05T14:00:00Z"));
        assertThat(result.expense().getDescription()).isEqualTo(transcription);
        assertThat(result.confirmationMessage())
                .isEqualTo("Gasto registrado: 45 reais e 90 centavos na categoria restaurante, incluído no mês de agosto.");
        assertThat(result.confirmationAudio()).containsExactly(9, 8, 7);

        ArgumentCaptor<Expense> expenseCaptor = ArgumentCaptor.forClass(Expense.class);
        verify(expenseRepository).save(expenseCaptor.capture());

        Expense persistedExpense = expenseCaptor.getValue();
        assertThat(persistedExpense.getCategory()).isEqualTo(ExpenseCategory.RESTAURANT);
        assertThat(persistedExpense.getDescription()).isEqualTo(transcription);
    }

    @Test
    void shouldPersistExpenseWithoutOccurredAtWhenNotInformed() {
        byte[] audioContent = new byte[]{1, 2, 3};
        String transcription = "Comprei remédio por 32 reais na farmácia";
        ParsedExpense parsedExpense = new ParsedExpense(
                Money.brl(new BigDecimal("32.00")),
                ExpenseCategory.PHARMACY,
                null
        );

        when(transcriptionService.transcribeExpenseAudio(audioContent, "recording.m4a"))
                .thenReturn(transcription);
        when(expenseExtractionService.extractFromTranscription(transcription))
                .thenReturn(parsedExpense);
        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(textToSpeechService.synthesizeSpeech(org.mockito.ArgumentMatchers.contains("Gasto registrado: 32 reais na categoria farmácia, incluído no mês de")))
                .thenReturn(new byte[]{1});

        ExpenseRegistrationResult result = expenseRegistrationService.registerFromAudio(
                audioContent,
                "recording.m4a"
        );

        assertThat(result.expense().getOccurredAt()).isNull();
        assertThat(result.expense().getCategory()).isEqualTo(ExpenseCategory.PHARMACY);
        assertThat(result.confirmationMessage())
                .startsWith("Gasto registrado: 32 reais na categoria farmácia, incluído no mês de ")
                .endsWith(".");
        assertThat(result.confirmationAudio()).containsExactly(1);
    }
}
