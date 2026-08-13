package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.model.ParsedExpense;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseParser;
import org.springframework.stereotype.Service;

@Service
public class ExpenseExtractionService {

    private final ExpenseParser expenseParser;

    public ExpenseExtractionService(ExpenseParser expenseParser) {
        this.expenseParser = expenseParser;
    }

    public ParsedExpense extractFromTranscription(String transcription) {
        if (transcription == null || transcription.isBlank()) {
            throw new IllegalArgumentException("transcription must not be blank");
        }

        return expenseParser.parse(transcription.strip());
    }
}
