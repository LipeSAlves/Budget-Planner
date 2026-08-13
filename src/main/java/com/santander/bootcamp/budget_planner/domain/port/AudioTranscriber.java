package com.santander.bootcamp.budget_planner.domain.port;

public interface AudioTranscriber {

    String transcribe(byte[] audioContent, String filename);
}
