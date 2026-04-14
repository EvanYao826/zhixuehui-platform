package com.tianji.ai.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class CourseRecommendationVO {
    private List<RecommendedCourse> courses;
    private String response;

    @Data
    public static class RecommendedCourse {
        private String courseName;
        private String description;
        private String reason;
        private int score;
    }
}