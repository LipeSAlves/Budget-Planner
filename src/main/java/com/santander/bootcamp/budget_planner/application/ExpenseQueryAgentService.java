package com.santander.bootcamp.budget_planner.application;

import com.santander.bootcamp.budget_planner.application.ExpenseQueryExecutionRecorder;
import com.santander.bootcamp.budget_planner.domain.port.ExpenseQueryInterpreter;
import org.springframework.stereotype.Service;

@Service
public class ExpenseQueryAgentService {

    private final TranscriptionService transcriptionService;
    private final ExpenseQueryInterpreter expenseQueryInterpreter;
    private final TextToSpeechService textToSpeechService;
    private final ExpenseQueryExecutionRecorder executionRecorder;

    public ExpenseQueryAgentService(
            TranscriptionService transcriptionService,
            ExpenseQueryInterpreter expenseQueryInterpreter,
            TextToSpeechService textToSpeechService,
            ExpenseQueryExecutionRecorder executionRecorder
    ) {
        this.transcriptionService = transcriptionService;
        this.expenseQueryInterpreter = expenseQueryInterpreter;
        this.textToSpeechService = textToSpeechService;
        this.executionRecorder = executionRecorder;
    }

    public ExpenseQueryAgentResult queryFromAudio(byte[] audioContent, String filename) {
        String transcription = transcriptionService.transcribeExpenseAudio(audioContent, filename);
        String answerText = expenseQueryInterpreter.answer(transcription);
        byte[] answerAudio = textToSpeechService.synthesizeSpeech(answerText);

        return new ExpenseQueryAgentResult(transcription, answerText, answerAudio);
    }

    public ExpenseQueryTextResult queryFromText(String question) {
        String normalizedQuestion = question.strip();
        String answerText = expenseQueryInterpreter.answer(normalizedQuestion);

        return new ExpenseQueryTextResult(
                normalizedQuestion,
                answerText,
                executionRecorder.getExecutions()
        );
    }
}
