package com.tianji.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.ai.AiClient;
import com.tianji.api.client.trade.TradeClient;
import com.tianji.api.dto.ai.CourseRecommendationDTO;
import com.tianji.common.domain.R;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.enums.UserType;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.RandomUtils;
import com.tianji.user.constants.UserConstants;
import com.tianji.user.domain.dto.StudentFormDTO;
import com.tianji.user.domain.po.User;
import com.tianji.user.domain.po.UserDetail;
import com.tianji.user.domain.query.UserPageQuery;
import com.tianji.user.domain.vo.StudentPageVo;
import com.tianji.user.service.IStudentService;
import com.tianji.user.service.IUserDetailService;
import com.tianji.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 学员详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements IStudentService {

    private final IUserService userService;
    private final IUserDetailService detailService;
    private final TradeClient tradeClient;
    private final AiClient aiClient;

    @Override
    @Transactional
    public void saveStudent(StudentFormDTO studentFormDTO) {
        // 1.新增用户账号
        User user = new User();
        user.setCellPhone(studentFormDTO.getCellPhone());
        user.setPassword(studentFormDTO.getPassword());
        user.setType(UserType.STUDENT);
        userService.addUserByPhone(user, studentFormDTO.getCode());

        // 2.新增学员详情
        UserDetail student = new UserDetail();
        student.setId(user.getId());
        student.setName(RandomUtils.randomString(8));
        student.setRoleId(UserConstants.STUDENT_ROLE_ID);
        detailService.save(student);
    }

    @Override
    public void updateMyPassword(StudentFormDTO studentFormDTO) {
        userService.updatePasswordByPhone(
                studentFormDTO.getCellPhone(), studentFormDTO.getCode(), studentFormDTO.getPassword()
        );
    }

    @Override
    public PageDTO<StudentPageVo> queryStudentPage(UserPageQuery query) {
        // 1.分页条件
        Page<UserDetail> page  =  detailService.queryUserDetailByPage(query, UserType.STUDENT);
        List<UserDetail> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 2.查询购买的课程数量
        List<Long> stuIds = records.stream().map(UserDetail::getId).collect(Collectors.toList());
        Map<Long, Integer> numMap = tradeClient.countEnrollCourseOfStudent(stuIds);

        // 3.处理vo
        List<StudentPageVo> list = new ArrayList<>(records.size());
        for (UserDetail r : records) {
            StudentPageVo v = BeanUtils.toBean(r, StudentPageVo.class);
            list.add(v);
            v.setCourseAmount(numMap.get(r.getId()));
        }
        return new PageDTO<>(page.getTotal(), page.getPages(), list);
    }

    @Override
    public Object getUserProfile(Long userId) {
        // 模拟获取用户画像，实际应该从ai_user_profile表查询
        // 这里暂时返回模拟数据
        Map<String, Object> profile = new java.util.HashMap<>();
        profile.put("userId", userId);
        profile.put("learningHistory", List.of("Java基础", "Spring Boot", "数据库原理"));
        profile.put("interestTags", List.of("编程", "后端开发", "云计算"));
        profile.put("learningGoal", "成为全栈工程师");
        return profile;
    }

    @Override
    public Object getCourseRecommendation(Long userId) {
        // 1.获取用户画像
        Map<String, Object> profile = (Map<String, Object>) getUserProfile(userId);
        
        // 2.构建课程推荐请求
        CourseRecommendationDTO dto = new CourseRecommendationDTO();
        dto.setLearningHistory((List<String>) profile.get("learningHistory"));
        dto.setInterestTags((List<String>) profile.get("interestTags"));
        dto.setLearningGoal((String) profile.get("learningGoal"));
        
        // 3.调用AI服务获取推荐
        R<?> result = aiClient.getCourseRecommendation(dto);
        return result.getData();
    }

    @Override
    public Object vectorSimilaritySearch(String query) {
        // 调用AI服务进行向量相似度搜索
        R<?> result = aiClient.vectorSimilaritySearch(query);
        return result.getData();
    }
}
