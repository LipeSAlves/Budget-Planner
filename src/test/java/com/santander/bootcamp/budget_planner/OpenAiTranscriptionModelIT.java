package com.santander.bootcamp.budget_planner;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiTranscriptionModelIT {

    @Autowired
    private TranscriptionModel transcriptionModel;

    private OpenAiAudioTranscriptionOptions transcriptionOptions() {
        return OpenAiAudioTranscriptionOptions.builder()
                .language("pt")
                .temperature(0f)
                .prompt("""
                      Transcreva os valores monetários mantendo a notação sempre em números (e.g.: em vez de transcrever 'cinco reais', transcreva 'R$5,00')
                      """)
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "audio/recording-1.m4a, R$80,00",
            "audio/recording-2.m4a, R$40,00",
            "audio/recording-3.m4a, R$120,00",
            "audio/recording-4.m4a, R$90,00",
            "audio/recording-5.m4a, R$200,00",
            "audio/recording-6.m4a, R$60,00"
    })
    void shouldTranscribeRecordingsAccuratelyInBRL(String audioPath, String expectedResult) {
        Resource audio = new ClassPathResource(audioPath);
        String transcription = transcriptionModel.transcribe(audio, transcriptionOptions());
        System.out.println(audioPath + " -> " + transcription);
        assertThat(transcription)
                .isNotBlank()
                .containsIgnoringCase(expectedResult);
    }
}
