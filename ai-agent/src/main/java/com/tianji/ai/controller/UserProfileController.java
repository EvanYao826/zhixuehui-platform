package com.tianji.ai.controller;

import com.tianji.ai.domain.po.UserProfile;
import com.tianji.ai.service.IUserProfileService;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.client.learning.LearningClient;
import com.tianji.api.dto.ai.UserProfileDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.common.domain.R;
import com.tianji.common.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户画像控制器
 */
@RestController
@RequestMapping("/ai/user")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final IUserProfileService userProfileService;
    private final LearningClient learningClient;
    private final CourseClient courseClient;
    
    /**
     * 根据用户ID获取用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    @GetMapping("/profile/{userId}")
    public R<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        // 1. 从数据库查询用户画像
        UserProfile profile = userProfileService.getByUserId(userId);
        
        // 2. 如果数据库中没有，从学习服务获取学习记录生成用户画像
        if (profile == null) {
            profile = generateUserProfileFromLearningRecords(userId);
            userProfileService.save(profile);
        }
        
        // 3. 转换为DTO返回
        UserProfileDTO dto = BeanUtils.toBean(profile, UserProfileDTO.class);
        return R.ok(dto);
    }
    
    /**
     * 从学习记录生成用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    private UserProfile generateUserProfileFromLearningRecords(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        
        try {
            // 由于LearningClient没有提供获取用户所有学习记录的方法，暂时使用默认用户画像
            // 后续可以集成其他服务获取用户的学习记录
            profile = generateDefaultUserProfile(userId);
        } catch (Exception e) {
            // 如果调用服务失败，使用默认用户画像
            e.printStackTrace();
            profile = generateDefaultUserProfile(userId);
        }
        
        return profile;
    }
    
    /**
     * 根据学习历史生成兴趣标签
     * @param learningHistory 学习历史
     * @return 兴趣标签
     */
    private String generateInterestTags(String learningHistory) {
        if (learningHistory.contains("Java") || learningHistory.contains("Spring")) {
            return "编程,后端开发,云计算";
        } else if (learningHistory.contains("HTML") || learningHistory.contains("JavaScript") || learningHistory.contains("React")) {
            return "前端开发,UI设计,响应式布局";
        } else if (learningHistory.contains("Python") || learningHistory.contains("数据")) {
            return "数据科学,人工智能,大数据";
        } else {
            return "编程,全栈开发,移动开发";
        }
    }
    
    /**
     * 根据学习历史生成学习目标
     * @param learningHistory 学习历史
     * @return 学习目标
     */
    private String generateLearningGoal(String learningHistory) {
        if (learningHistory.contains("Java") || learningHistory.contains("Spring")) {
            return "成为后端架构师";
        } else if (learningHistory.contains("HTML") || learningHistory.contains("JavaScript") || learningHistory.contains("React")) {
            return "成为前端架构师";
        } else if (learningHistory.contains("Python") || learningHistory.contains("数据")) {
            return "成为数据科学家";
        } else {
            return "成为全栈开发工程师";
        }
    }
    
    /**
     * 保存用户画像
     * @param dto 用户画像DTO
     * @return 保存结果
     */
    @PostMapping("/profile")
    public R<Boolean> saveUserProfile(@RequestBody UserProfileDTO dto) {
        // 1. 转换为PO
        UserProfile profile = BeanUtils.toBean(dto, UserProfile.class);
        
        // 2. 保存到数据库
        userProfileService.save(profile);
        
        return R.ok(true);
    }
    
    /**
     * 生成默认用户画像
     * @param userId 用户ID
     * @return 默认用户画像
     */
    private UserProfile generateDefaultUserProfile(Long userId) {
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        
        // 根据不同的userId生成不同的默认用户画像
        if (userId == 1) {
            // 用户1：前端开发方向
            profile.setLearningHistory("HTML/CSS,JavaScript,React");
            profile.setInterestTags("前端开发,UI设计,响应式布局");
            profile.setLearningGoal("成为前端架构师");
        } else if (userId == 2) {
            // 用户2：后端开发方向
            profile.setLearningHistory("Java基础,Spring Boot,数据库原理");
            profile.setInterestTags("编程,后端开发,云计算");
            profile.setLearningGoal("成为全栈工程师");
        } else if (userId == 3) {
            // 用户3：数据科学方向
            profile.setLearningHistory("Python,数据分析,机器学习");
            profile.setInterestTags("数据科学,人工智能,大数据");
            profile.setLearningGoal("成为数据科学家");
        } else {
            // 默认用户：全栈开发方向
            profile.setLearningHistory("Java基础,HTML/CSS,JavaScript");
            profile.setInterestTags("编程,全栈开发,移动开发");
            profile.setLearningGoal("成为全栈开发工程师");
        }
        
        return profile;
    }
}
