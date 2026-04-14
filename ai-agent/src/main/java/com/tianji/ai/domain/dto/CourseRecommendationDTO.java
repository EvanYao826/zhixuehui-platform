package com.tianji.ai.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseRecommendationDTO {
    private String userId;
    private List<String> learningHistory;
    private List<String> interestTags;
    private String learningGoal;
}