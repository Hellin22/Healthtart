package com.dev5ops.healthtart.gpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface GptService {
    String generateRoutine(String userCode, String bodyPart, int time);

    Map<String, Object> routineParser(String response) throws JsonProcessingException;

    Long processRoutine(String response) throws JsonProcessingException;
}
