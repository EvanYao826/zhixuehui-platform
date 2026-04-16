package com.tianji.api.client.ai.fallback;

import com.tianji.api.client.ai.UserProfileClient;
import com.tianji.api.dto.ai.UserProfileDTO;
import com.tianji.common.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户画像Feign客户端降级类
 */
@Slf4j
@Component
public class UserProfileClientFallback implements UserProfileClient {
    
    @Override
    public R<UserProfileDTO> getUserProfile(Long userId) {
        log.error("调用AI服务获取用户画像失败，返回默认用户画像");
        // 返回默认用户画像
        UserProfileDTO profile = new UserProfileDTO();
        profile.setUserId(userId);
        profile.setLearningHistory("Java基础,HTML/CSS,JavaScript");
        profile.setInterestTags("编程,全栈开发,移动开发");
        profile.setLearningGoal("成为全栈开发工程师");
        return R.ok(profile);
    }
    
    @Override
    public R<Boolean> saveUserProfile(UserProfileDTO profile) {
        log.error("调用AI服务保存用户画像失败");
        return R.ok(false);
    }
}
