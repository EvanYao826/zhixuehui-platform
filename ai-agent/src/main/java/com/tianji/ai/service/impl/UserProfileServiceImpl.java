package com.tianji.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.ai.domain.po.UserProfile;
import com.tianji.ai.mapper.UserProfileMapper;
import com.tianji.ai.service.IUserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户画像服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements IUserProfileService {
    
    private final UserProfileMapper userProfileMapper;
    
    @Override
    public UserProfile getByUserId(Long userId) {
        return userProfileMapper.selectByUserId(userId);
    }
    
    @Override
    public boolean save(UserProfile profile) {
        return baseMapper.insert(profile) > 0;
    }
}
