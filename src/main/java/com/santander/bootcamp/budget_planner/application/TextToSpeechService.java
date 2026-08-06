package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.port.SpeechSynthesizer;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {

    private final SpeechSynthesizer speechSynthesizer;

    public TextToSpeechService(SpeechSynthesizer speechSynthesizer) {
        this.speechSynthesizer = speechSynthesizer;
    }

    public byte[] synthesizeSpeech(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }

        return speechSynthesizer.synthesize(text.strip());
    }
}
