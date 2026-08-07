package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.ExpenseRegistrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRegistrationService expenseRegistrationService;

    public ExpenseController(ExpenseRegistrationService expenseRegistrationService) {
        this.expenseRegistrationService = expenseRegistrationService;
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
}
