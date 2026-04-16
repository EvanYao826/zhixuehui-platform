package com.tianji.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianji.ai.domain.po.UserProfile;

/**
 * 用户画像Mapper
 */
public interface UserProfileMapper extends BaseMapper<UserProfile> {
    
    /**
     * 根据用户ID查询用户画像
     * @param userId 用户ID
     * @return 用户画像
     */
    UserProfile selectByUserId(Long userId);
}
