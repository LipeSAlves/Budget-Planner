package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.AudioTranscriber;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseParser;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import com.santander.bootcamp.budget_planner.domain.port.SpeechSynthesizer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class ExpenseRegistrationServiceIT {

    @Autowired
    private ExpenseRegistrationService expenseRegistrationService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldPersistExpenseThroughFullRegistrationFlow() {
        byte[] audioContent = new byte[]{1, 2, 3};

        ExpenseRegistrationResult result = expenseRegistrationService.registerFromAudio(
                audioContent,
                "recording.m4a"
        );

        entityManager.flush();
        entityManager.clear();

        assertThat(result.transcription()).isEqualTo("Gastei 80 reais no mercado");
        assertThat(result.expense().getCategory()).isEqualTo(ExpenseCategory.GROCERIES);
        assertThat(result.expense().getMoney().amount()).isEqualByComparingTo("80.00");
        assertThat(result.expense().getDescription()).isEqualTo("Gastei 80 reais no mercado");
        assertThat(result.expense().getOccurredAt()).isNull();
        assertThat(result.confirmationMessage()).isEqualTo("Gasto registrado: 80 reais na categoria mercado.");
        assertThat(result.confirmationAudio()).isNotEmpty();

        assertThat(expenseRepository.findById(result.expense().getId()))
                .isPresent()
                .get()
                .satisfies(found -> assertThat(found.getCategory()).isEqualTo(ExpenseCategory.GROCERIES));
    }

    @TestConfiguration
    static class StubAiConfiguration {

        @Bean
        @Primary
        AudioTranscriber audioTranscriber() {
            AudioTranscriber audioTranscriber = mock(AudioTranscriber.class);
            when(audioTranscriber.transcribe(any(), anyString()))
                    .thenReturn("Gastei 80 reais no mercado");
            return audioTranscriber;
        }

        @Bean
        @Primary
        ExpenseParser expenseParser() {
            ExpenseParser expenseParser = mock(ExpenseParser.class);
            when(expenseParser.parse(anyString()))
                    .thenReturn(new ParsedExpense(
                            Money.brl(new BigDecimal("80.00")),
                            ExpenseCategory.GROCERIES,
                            null
                    ));
            return expenseParser;
        }

        @Bean
        @Primary
        SpeechSynthesizer speechSynthesizer() {
            SpeechSynthesizer speechSynthesizer = mock(SpeechSynthesizer.class);
            when(speechSynthesizer.synthesize(anyString()))
                    .thenReturn(new byte[]{1, 2, 3});
            return speechSynthesizer;
        }
    }
}
