package com.tianji.api.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class CourseRecommendationDTO {
    private List<String> learningHistory;
    private List<String> interestTags;
    private String learningGoal;
}
