package com.tianji.ai.domain.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AdminAssistantDTO {
    private Map<String, Object> platformData;
    private String adminQuestion;
}