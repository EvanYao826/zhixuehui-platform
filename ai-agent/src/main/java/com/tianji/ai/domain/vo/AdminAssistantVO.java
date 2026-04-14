package com.tianji.ai.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminAssistantVO {
    private String dataAnalysis;
    private List<String> trendAnalysis;
    private String questionAnswer;
    private List<String> suggestions;
}