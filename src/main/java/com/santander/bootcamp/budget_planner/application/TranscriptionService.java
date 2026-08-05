package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.port.AudioTranscriber;
import org.springframework.stereotype.Service;

@Service
public class TranscriptionService {

    private static final String DEFAULT_AUDIO_FILENAME = "recording.m4a";

    private final AudioTranscriber audioTranscriber;

    public TranscriptionService(AudioTranscriber audioTranscriber) {
        this.audioTranscriber = audioTranscriber;
    }

    public String transcribeExpenseAudio(byte[] audioContent, String filename) {
        if (audioContent == null || audioContent.length == 0) {
            throw new IllegalArgumentException("audio file must not be empty");
        }

        String resolvedFilename = (filename == null || filename.isBlank())
                ? DEFAULT_AUDIO_FILENAME
                : filename;

        return audioTranscriber.transcribe(audioContent, resolvedFilename);
    }
}
