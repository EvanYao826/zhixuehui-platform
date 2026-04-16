package com.tianji.api.client.ai;

import com.tianji.api.client.ai.fallback.UserProfileClientFallback;
import com.tianji.api.dto.ai.UserProfileDTO;
import com.tianji.common.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户画像Feign客户端
 */
@FeignClient(value = "ai-agent", contextId = "userProfileClient", fallback = UserProfileClientFallback.class)
public interface UserProfileClient {
    
    /**
     * 根据用户ID获取用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    @GetMapping("/ai/user/profile/{userId}")
    R<UserProfileDTO> getUserProfile(@PathVariable("userId") Long userId);
    
    /**
     * 保存用户画像
     * @param profile 用户画像
     * @return 保存结果
     */
    @PostMapping("/ai/user/profile")
    R<Boolean> saveUserProfile(@RequestBody UserProfileDTO profile);
}
