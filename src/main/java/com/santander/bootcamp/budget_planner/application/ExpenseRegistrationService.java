package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class ExpenseRegistrationService {

    private final TranscriptionService transcriptionService;
    private final ExpenseExtractionService expenseExtractionService;
    private final ExpenseRepository expenseRepository;

    public ExpenseRegistrationService(
            TranscriptionService transcriptionService,
            ExpenseExtractionService expenseExtractionService,
            ExpenseRepository expenseRepository
    ) {
        this.transcriptionService = transcriptionService;
        this.expenseExtractionService = expenseExtractionService;
        this.expenseRepository = expenseRepository;
    }

    public ExpenseRegistrationResult registerFromAudio(byte[] audioContent, String filename) {
        String transcription = transcriptionService.transcribeExpenseAudio(audioContent, filename);
        ParsedExpense parsedExpense = expenseExtractionService.extractFromTranscription(transcription);

        Expense expense = Expense.create(
                parsedExpense.category(),
                parsedExpense.money(),
                parsedExpense.occurredAt(),
                transcription
        );

        Expense savedExpense = expenseRepository.save(expense);

        return new ExpenseRegistrationResult(transcription, savedExpense);
    }
}
