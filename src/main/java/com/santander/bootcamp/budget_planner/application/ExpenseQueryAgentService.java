package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.domain.port.ExpenseQueryInterpreter;
import org.springframework.stereotype.Service;

@Service
public class ExpenseQueryAgentService {

    private final TranscriptionService transcriptionService;
    private final ExpenseQueryInterpreter expenseQueryInterpreter;
    private final TextToSpeechService textToSpeechService;

    public ExpenseQueryAgentService(
            TranscriptionService transcriptionService,
            ExpenseQueryInterpreter expenseQueryInterpreter,
            TextToSpeechService textToSpeechService
    ) {
        this.transcriptionService = transcriptionService;
        this.expenseQueryInterpreter = expenseQueryInterpreter;
        this.textToSpeechService = textToSpeechService;
    }

    public ExpenseQueryAgentResult queryFromAudio(byte[] audioContent, String filename) {
        String transcription = transcriptionService.transcribeExpenseAudio(audioContent, filename);
        String answerText = expenseQueryInterpreter.answer(transcription);
        byte[] answerAudio = textToSpeechService.synthesizeSpeech(answerText);

        return new ExpenseQueryAgentResult(transcription, answerText, answerAudio);
    }

    public ExpenseQueryAgentResult queryFromText(String question) {
        String answerText = expenseQueryInterpreter.answer(question);
        byte[] answerAudio = textToSpeechService.synthesizeSpeech(answerText);

        return new ExpenseQueryAgentResult(question, answerText, answerAudio);
    }
}
