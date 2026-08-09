package com.santander.bootcamp.budget_planner.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record QueryExpenseFromTextRequest(
        @NotBlank String question
) {
}
