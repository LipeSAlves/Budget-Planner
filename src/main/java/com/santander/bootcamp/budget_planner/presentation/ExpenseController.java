package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.ExpenseManagementService;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryAgentService;
import com.santander.bootcamp.budget_planner.application.ExpenseQueryService;
import com.santander.bootcamp.budget_planner.application.ExpenseRegistrationService;
import com.santander.bootcamp.budget_planner.domain.model.ExpenseCategory;
import com.santander.bootcamp.budget_planner.presentation.dto.ExpenseNaturalLanguageQueryResponse;
import com.santander.bootcamp.budget_planner.presentation.dto.ExpenseQueryResponse;
import com.santander.bootcamp.budget_planner.presentation.dto.ExpenseResponse;
import com.santander.bootcamp.budget_planner.presentation.dto.QueryExpenseFromTextRequest;
import com.santander.bootcamp.budget_planner.presentation.dto.RegisterExpenseFromTextRequest;
import com.santander.bootcamp.budget_planner.presentation.dto.UpdateExpenseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRegistrationService expenseRegistrationService;
    private final ExpenseQueryService expenseQueryService;
    private final ExpenseQueryAgentService expenseQueryAgentService;
    private final ExpenseManagementService expenseManagementService;

    public ExpenseController(
            ExpenseRegistrationService expenseRegistrationService,
            ExpenseQueryService expenseQueryService,
            ExpenseQueryAgentService expenseQueryAgentService,
            ExpenseManagementService expenseManagementService
    ) {
        this.expenseRegistrationService = expenseRegistrationService;
        this.expenseQueryService = expenseQueryService;
        this.expenseQueryAgentService = expenseQueryAgentService;
        this.expenseManagementService = expenseManagementService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseQueryResponse> listExpenses(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) ExpenseCategory category
    ) {
        var result = category == null
                ? expenseQueryService.findByMonth(year, month)
                : expenseQueryService.findByMonthAndCategory(year, month, category);

        return ResponseEntity.ok(ExpenseQueryResponse.from(result));
    }

    @PostMapping(path = "/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseNaturalLanguageQueryResponse> queryFromText(
            @Valid @RequestBody QueryExpenseFromTextRequest request
    ) {
        var result = expenseQueryAgentService.queryFromText(request.question());

        return ResponseEntity.ok(ExpenseNaturalLanguageQueryResponse.from(result));
    }

    @PostMapping(path = "/query", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<byte[]> queryFromAudio(@RequestPart("audio") MultipartFile audio) throws IOException {
        var result = expenseQueryAgentService.queryFromAudio(
                audio.getBytes(),
                audio.getOriginalFilename()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"expense-query.mp3\"")
                .body(result.answerAudio());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseResponse> registerFromText(@Valid @RequestBody RegisterExpenseFromTextRequest request) {
        var expense = expenseRegistrationService.registerFromText(request.text());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ExpenseResponse.from(expense));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mpeg")
    public ResponseEntity<byte[]> registerFromAudio(@RequestPart("audio") MultipartFile audio) throws IOException {
        var result = expenseRegistrationService.registerFromAudio(
                audio.getBytes(),
                audio.getOriginalFilename()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header("X-Expense-Id", result.expense().getId().value().toString())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"expense-confirmation.mp3\"")
                .body(result.confirmationAudio());
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest request
    ) {
        return expenseManagementService.update(
                id,
                request.category(),
                request.amount(),
                request.currency(),
                request.occurredAt(),
                request.description()
        )
                .map(expense -> ResponseEntity.ok(ExpenseResponse.from(expense)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        if (!expenseManagementService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
