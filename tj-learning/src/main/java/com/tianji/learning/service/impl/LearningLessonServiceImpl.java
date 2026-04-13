package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.IdAndNumDTO;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.*;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.PlanStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2026-01-29
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    private final CourseClient courseClient;

    private final CatalogueClient catalogueClient;

    private final LearningRecordMapper learningRecordMapper;

    /**
     * 添加用户课程
     * @param userId 用户id
     * @param courseIds 课程id
     */
    @Override
    @Transactional
    public void addUserLessons(Long userId, List<Long> courseIds) {

        // 1.查询课程有效期
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(courseIds);
        if (CollUtils.isEmpty(cInfoList)){
            // 异常处理（课程不存在，无法添加）
            log.error("课程信息不存在，添加失败");
            return;
        }

        // 2.循环遍历，处理LearningLesson数据
        List<LearningLesson> list = new ArrayList<>(cInfoList.size());
        for (CourseSimpleInfoDTO cInfo : cInfoList) {
            LearningLesson lesson = new LearningLesson();
            // 2.1获取过期时间

            Integer validDuration = cInfo.getValidDuration();
            if (validDuration != null && validDuration > 0) {
                LocalDateTime now = LocalDateTime.now();
                lesson.setCreateTime(now);
                lesson.setExpireTime(now.plusMonths(validDuration));
            }
            // 2.2填充 UserId和 CourseId
            lesson.setUserId(userId);
            lesson.setCourseId(cInfo.getId());
            list.add(lesson);
        }
        // 3.批量插入
        saveBatch(list);
    }

    /**
     * 查询我的课表
     * @param query 分页参数
     * @return 我的课表
     */
    @Override
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();

        // 2.分页查询
         Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .page(query.toMpPage("latest_learn_time",false));
          List<LearningLesson> records =page.getRecords();
          if (CollUtils.isEmpty(records)){
              return PageDTO.empty(page);
          }
        // 3.查询课程信息
        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);
        // 4.封装成VO 返回
        List<LearningLessonVO> list = new ArrayList<>(records.size());
        // 4.1循环遍历,把LearningLesson转换成LearningLessonVO
        for (LearningLesson lesson : records) {
            //4.2.拷贝基础属性到VO
            LearningLessonVO vo = BeanUtils.toBean(lesson, LearningLessonVO.class);
            //4.3.获取课程信息，填充VO
            CourseSimpleInfoDTO cInfo = cMap.get(lesson.getCourseId());
            vo.setCourseName(cInfo.getName());
            vo.setCourseCoverUrl(cInfo.getCoverUrl());
            vo.setSections(cInfo.getSectionNum());
            list.add(vo);
        }

        return PageDTO.of(page, list);
    }

    private Map<Long, CourseSimpleInfoDTO> queryCourseSimpleInfoList(List<LearningLesson> records) {
        // 3.1获取课程id
        Set<Long> cIds = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        // 3.2查询课程信息
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(cIds);
        if (CollUtils.isEmpty(cInfoList)){
            // 异常处理（课程不存在，无法添加）
            throw new BadRequestException("课程信息不存在!!!");
        }
        // 3.3把课程集合处理成map，key是课程id，value是课程信息
        Map<Long, CourseSimpleInfoDTO> cMap = cInfoList.stream()
                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        return cMap;
    }

    /**
     * 查询正在学习的课程
     * @return 正在学习的课程
     */
    @Override
    public LearningLessonVO queryNowLearningLesson() {
        // 1.获取当前登录的用户
        Long userId = UserContext.getUser();
        // 2.查询正在学习的课程 select * from xx where user_id = #{userId} AND status = 1 order by latest_learn_time limit 1
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if (lesson == null) {
            return null;
        }
        // 3.拷贝PO基础属性到VO
        LearningLessonVO vo = BeanUtils.copyBean(lesson, LearningLessonVO.class);
        // 4.查询课程信息
        CourseFullInfoDTO cInfo = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (cInfo == null) {
            throw new BadRequestException("课程不存在");
        }
        vo.setCourseName(cInfo.getName());
        vo.setCourseCoverUrl(cInfo.getCoverUrl());
        vo.setSections(cInfo.getSectionNum());
        // 5.统计课表中的课程数量 select count(1) from xxx where user_id = #{userId}
        Integer courseAmount = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .count();
        vo.setCourseAmount(courseAmount);
        // 6.查询小节信息
        List<CataSimpleInfoDTO> cataInfos =
                catalogueClient.batchQueryCatalogue(CollUtils.singletonList(lesson.getLatestSectionId()));
        if (!CollUtils.isEmpty(cataInfos)) {
            CataSimpleInfoDTO cataInfo = cataInfos.get(0);
            vo.setLatestSectionName(cataInfo.getName());
            vo.setLatestSectionIndex(cataInfo.getCIndex());
        }
        return vo;
    }

    /**
     * 根据课程id查询课程信息
     * @param courseId 课程id
     * @return 课程信息
     */
    @Override
    public LearningLessonVO queryLessonsByCourseId(Long courseId) {
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();

        // 2.查询课程信息
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();

        if (lesson == null) {
            return null; // 如果未找到课程信息，直接返回null
        }

        // 3.将LearningLesson转换为LearningLessonVO
        LearningLessonVO vo = BeanUtils.toBean(lesson, LearningLessonVO.class);

        // 4.查询课程详细信息并填充到VO中
        CourseFullInfoDTO courseInfo = courseClient.getCourseInfoById(courseId, false, false);
        if (courseInfo != null) {
            vo.setCourseName(courseInfo.getName());
            vo.setCourseCoverUrl(courseInfo.getCoverUrl());
            vo.setSections(courseInfo.getSectionNum());
        }

        return vo;
    }


    /**
     * 删除用户退款课程信息
     * @param userId 用户ID
     * @param courseIds 课程ID
     */
    @Override
    @Transactional
    public void removeUserLessons(Long userId, List<Long> courseIds) {
      if (CollUtils.isEmpty(courseIds)){
          // 如果课程ID为空，则不处理
          return;
      }
      // 删除条件：当前用户 + 指定课程ID
      lambdaUpdate()
              .eq(LearningLesson::getUserId, userId)
              .in(LearningLesson::getCourseId, courseIds)
              .remove();
    }

    /**
     * 判断课程是否有效
     * @param courseId 课程ID
     * @return 课程ID
     */
    @Override
    public Long isLessonValid(Long courseId) {
        // 1. 获取当前登录用户
        Long userId = UserContext.getUser();

        // 2. 查询用户的课程记录
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();

        // 3. 如果没有找到课程记录，返回null
        if (lesson == null) {
            return null;
        }

        // 4. 检查课程是否已过期
        LocalDateTime expireTime = lesson.getExpireTime();
        if (expireTime != null && expireTime.isBefore(LocalDateTime.now())) {
            // 课程已过期，返回null表示无效
            return null;
        }

        // 5. 课程有效，返回课程ID
        return courseId;
    }

    /**
     * 统计课程的已学人数
     * @param courseId 课程ID
     * @return 已学人数
     */
    @Override
    public Integer countLearningLessonByCourse(Long courseId) {

        // 2. 查询已学人数
        return lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .count();
    }

    /**
     * 根据用户ID和课程ID查询学习记录
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return 学习记录
     */
    @Override
    public LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId) {
        return lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();
    }

    /**
     * 创建学习计划
     * @param courseId 课程ID
     * @param freq 周频度
     */
    @Override
    public void createLearningPlan(Long courseId, Integer freq) {
        // 1. 获取当前登录用户
        Long userId = UserContext.getUser();
        // 2. 查询指定课程相关数据
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getCourseId, courseId)
                .one();
        AssertUtils.isNotNull(lesson, "课程信息不存在!!!");
        // 3. 修改数据
        LearningLesson lUpdate = new LearningLesson();
        lUpdate.setWeekFreq(freq);
        lUpdate.setId(lesson.getId());
        if (lesson.getPlanStatus().equals(PlanStatus.NO_PLAN.getValue())) {
            lUpdate.setPlanStatus(PlanStatus.PLAN_RUNNING.getValue());
        }

        updateById(lUpdate);
    }

    /**
     * 查询我的学习计划
     * @param query 分页参数
     * @return 学习计划列表
     */
    @Override
    public LearningPlanPageVO queryMyPlans(PageQuery query) {
        LearningPlanPageVO result = new LearningPlanPageVO();
        // 1.获取当前登录用户
        Long userId = UserContext.getUser();
        // 2.获取本周起始时间
        LocalDateTime weekBeginTime =DateUtils.getWeekBeginTime(LocalDate.now());
        LocalDateTime weekEndTime =DateUtils.getWeekEndTime(LocalDate.now());
        // 3.查询总的计划数据
        // 3.1 查询本周已学习的数据
        Integer weekFinished = learningRecordMapper.selectCount(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished,true)
                .gt(LearningRecord::getFinishTime, weekBeginTime)
                .lt(LearningRecord::getFinishTime, weekEndTime));
        result.setWeekFinished(weekFinished);
        // 3.2 查询本周总的学习计划
        Integer weekTotalPlan = getBaseMapper().queryTotalPlan(userId);
        result.setWeekTotalPlan(weekTotalPlan);
        // TODO 3.3 本周学习积分

        // 4.查询分页数据
        // 4.1 分页查询课表信息和学习计划信息
         Page<LearningLesson> p = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING.getValue())
                .in(LearningLesson::getStatus, LessonStatus.NOT_BEGIN, LessonStatus.LEARNING)
                .page(query.toMpPage("latest_learn_time",false));
         List<LearningLesson> records = p.getRecords();
         if (CollUtils.isEmpty(records)){
             return result.pageInfo(PageDTO.empty( p));
         }
        // 4.2 查询课表对应的课程信息
        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);
        // 4.3 统计每一个课程本周已学习的小节数
        List<IdAndNumDTO> list = learningRecordMapper.countLearnedSections(userId, weekBeginTime, weekEndTime);
        Map<Long, Integer> cNumMap = IdAndNumDTO.toMap(list);
        // 4.4 组装数据VO
        List<LearningPlanVO> voList = new ArrayList<>(records.size());
        for (LearningLesson lesson : records) {
            // 4.4.1 拷贝基础属性
            LearningPlanVO vo = BeanUtils.copyBean(lesson, LearningPlanVO.class);
            // 4.4.2 填充课程详细信息
            CourseSimpleInfoDTO cInfo = cMap.get(lesson.getCourseId());
            if (cInfo != null){
                vo.setCourseName(cInfo.getName());
                vo.setSections(cInfo.getSectionNum());
            }
            // 4.4.3 填充已学小节数
            vo.setWeekLearnedSections(cNumMap.getOrDefault(lesson.getId(), 0));
            voList.add(vo);
        }
        return result.pageInfo(p.getTotal(), p.getPages(), voList);
    }
}
