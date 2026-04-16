package com.tianji.api.dto.ai;

import lombok.Data;

/**
 * 用户画像DTO
 */
@Data
public class UserProfileDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 学习历史，逗号分隔
     */
    private String learningHistory;
    
    /**
     * 兴趣标签，逗号分隔
     */
    private String interestTags;
    
    /**
     * 学习目标
     */
    private String learningGoal;
}
