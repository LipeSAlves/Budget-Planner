package com.santander.bootcamp.budget_planner.infrastructure.ai;

import com.santander.bootcamp.budget_planner.domain.port.SpeechSynthesizer;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.stereotype.Component;

@Component
public class SpringAiSpeechSynthesizer implements SpeechSynthesizer {

    private final TextToSpeechModel textToSpeechModel;

    public SpringAiSpeechSynthesizer(TextToSpeechModel textToSpeechModel) {
        this.textToSpeechModel = textToSpeechModel;
    }

    @Override
    public byte[] synthesize(String text) {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions.builder()
                .responseFormat(OpenAiAudioSpeechOptions.AudioResponseFormat.MP3)
                .voice(OpenAiAudioSpeechOptions.Voice.NOVA)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(text, options);

        return textToSpeechModel.call(prompt).getResult().getOutput();
    }
}
