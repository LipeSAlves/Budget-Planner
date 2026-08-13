package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.domain.port.AudioTranscriber;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class SpringAiAudioTranscriber implements AudioTranscriber {

    private final TranscriptionModel transcriptionModel;

    public SpringAiAudioTranscriber(TranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @Override
    public String transcribe(byte[] audioContent, String filename) {
        Resource audio = new ByteArrayResource(audioContent) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions.builder()
                .language("pt")
                .temperature(0f)
                .prompt("""
                        Transcreva os valores monetários mantendo a notação sempre em números (e.g.: em vez de transcrever 'cinco reais', transcreva 'R$5,00')
                        """)
                .build();

        return transcriptionModel.transcribe(audio, options);
    }
}
