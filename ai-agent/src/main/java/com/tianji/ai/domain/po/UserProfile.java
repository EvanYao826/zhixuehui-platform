package com.tianji.ai.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户画像表
 */
@Data
@Accessors(chain = true)
@TableName("ai_user_profile")
public class UserProfile {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
