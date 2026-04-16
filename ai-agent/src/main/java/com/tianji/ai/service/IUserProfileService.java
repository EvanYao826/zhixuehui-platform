package com.tianji.ai.service;

import com.tianji.ai.domain.po.UserProfile;

/**
 * 用户画像服务接口
 */
public interface IUserProfileService {
    
    /**
     * 根据用户ID获取用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    UserProfile getByUserId(Long userId);
    
    /**
     * 保存用户画像
     * @param profile 用户画像
     * @return 保存结果
     */
    boolean save(UserProfile profile);
}
