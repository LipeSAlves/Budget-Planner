package com.santander.bootcamp.budget_planner.presentation;

import com.santander.bootcamp.budget_planner.application.TextToSpeechService;
import com.santander.bootcamp.budget_planner.presentation.dto.TextToSpeechRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/speech")
public class TextToSpeechController {

    private final TextToSpeechService textToSpeechService;

    public TextToSpeechController(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    @PostMapping(produces = "audio/mpeg")
    public ResponseEntity<byte[]> synthesize(@Valid @RequestBody TextToSpeechRequest request) {
        byte[] audio = textToSpeechService.synthesizeSpeech(request.text());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"")
                .body(audio);
    }
}
