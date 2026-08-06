package com.santander.bootcamp.budget_planner.domain.port;

public interface SpeechSynthesizer {

    byte[] synthesize(String text);
}
