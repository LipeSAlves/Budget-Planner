package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.ExpenseManagementService;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryAgentResult;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryAgentService;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryResult;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryService;
import com.santander.bootcamp.budget_planner.application.ExpenseRegistrationService;
import com.santander.bootcamp.budget_planner.domain.model.Expense;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.domain.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseRegistrationService expenseRegistrationService;

    @Mock
    private ExpenseQueryService expenseQueryService;

    @Mock
    private ExpenseQueryAgentService expenseQueryAgentService;

    @Mock
    private ExpenseManagementService expenseManagementService;

    @InjectMocks
    private ExpenseController expenseController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(expenseController)
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldListExpensesByMonthAndCategory() throws Exception {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                Instant.parse("2026-07-15T15:00:00Z"),
                "Almoço"
        );

        when(expenseQueryService.findByMonthAndCategory(2026, 7, ExpenseCategory.RESTAURANT))
                .thenReturn(new ExpenseQueryResult(2026, 7, ExpenseCategory.RESTAURANT, List.of(expense), new BigDecimal("45.90")));

        mockMvc.perform(get("/api/expenses")
                        .param("year", "2026")
                        .param("month", "7")
                        .param("category", "RESTAURANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.month").value(7))
                .andExpect(jsonPath("$.categoryFilter").value("RESTAURANT"))
                .andExpect(jsonPath("$.totalAmount").value(45.90))
                .andExpect(jsonPath("$.expenses[0].category").value("RESTAURANT"))
                .andExpect(jsonPath("$.expenses[0].amount").value(45.90))
                .andExpect(jsonPath("$.expenses[0].description").value("Almoço"));
    }

    @Test
    void shouldListExpensesByMonthWithoutCategoryFilter() throws Exception {
        when(expenseQueryService.findByMonth(2026, 6))
                .thenReturn(new ExpenseQueryResult(2026, 6, null, List.of(), BigDecimal.ZERO));

        mockMvc.perform(get("/api/expenses")
                        .param("year", "2026")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryFilter").isEmpty())
                .andExpect(jsonPath("$.expenses").isEmpty())
                .andExpect(jsonPath("$.totalAmount").value(0));
    }

    @Test
    void shouldQueryExpensesFromAudio() throws Exception {
        when(expenseQueryAgentService.queryFromAudio(any(byte[].class), eq("recording.m4a")))
                .thenReturn(new ExpenseQueryAgentResult(
                        "Quanto gastei em restaurante em julho?",
                        "Você gastou 45 reais em restaurante em julho de 2026.",
                        new byte[]{1, 2, 3}
                ));

        mockMvc.perform(multipart("/api/expenses/query")
                        .file(new MockMultipartFile(
                                "audio",
                                "recording.m4a",
                                "audio/m4a",
                                new byte[]{1, 2, 3}
                        )))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "audio/mpeg"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"expense-query.mp3\""));
    }

    @Test
    void shouldRegisterExpenseFromText() throws Exception {
        Expense expense = Expense.create(
                ExpenseCategory.RESTAURANT,
                Money.brl(new BigDecimal("45.90")),
                null,
                "Gastei 45 reais no restaurante"
        );

        when(expenseRegistrationService.registerFromText("Gastei 45 reais no restaurante"))
                .thenReturn(expense);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text": "Gastei 45 reais no restaurante"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("RESTAURANT"))
                .andExpect(jsonPath("$.amount").value(45.90))
                .andExpect(jsonPath("$.description").value("Gastei 45 reais no restaurante"));
    }

    @Test
    void shouldUpdateExpense() throws Exception {
        Expense expense = Expense.create(
                ExpenseCategory.GROCERIES,
                Money.brl(new BigDecimal("80.00")),
                Instant.parse("2026-07-15T12:00:00Z"),
                "Compras no mercado"
        );
        UUID id = expense.getId().value();

        when(expenseManagementService.update(
                eq(id),
                eq(ExpenseCategory.GROCERIES),
                eq(new BigDecimal("80.00")),
                eq("BRL"),
                eq(Instant.parse("2026-07-15T12:00:00Z")),
                eq("Compras no mercado")
        )).thenReturn(java.util.Optional.of(expense));

        mockMvc.perform(put("/api/expenses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "GROCERIES",
                                  "amount": 80.00,
                                  "currency": "BRL",
                                  "occurredAt": "2026-07-15T12:00:00Z",
                                  "description": "Compras no mercado"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("GROCERIES"))
                .andExpect(jsonPath("$.amount").value(80.00))
                .andExpect(jsonPath("$.description").value("Compras no mercado"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUnknownExpense() throws Exception {
        UUID id = UUID.randomUUID();

        when(expenseManagementService.update(
                eq(id),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/expenses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "OTHER",
                                  "amount": 10.00
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        UUID id = UUID.randomUUID();

        when(expenseManagementService.delete(id)).thenReturn(true);

        mockMvc.perform(delete("/api/expenses/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownExpense() throws Exception {
        UUID id = UUID.randomUUID();

        when(expenseManagementService.delete(id)).thenReturn(false);

        mockMvc.perform(delete("/api/expenses/{id}", id))
                .andExpect(status().isNotFound());
    }
}
