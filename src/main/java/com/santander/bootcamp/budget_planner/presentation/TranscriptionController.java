package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.TranscriptionService;
import com.santander.bootcamp.budget_planner.presentation.dto.TranscriptionResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/transcription")
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    public TranscriptionController(TranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptionResponse> transcribe(@RequestPart("audio") MultipartFile audio)
            throws IOException {
        String transcription = transcriptionService.transcribeExpenseAudio(
                audio.getBytes(),
                audio.getOriginalFilename()
        );

        return ResponseEntity.ok(new TranscriptionResponse(transcription));
    }
}
