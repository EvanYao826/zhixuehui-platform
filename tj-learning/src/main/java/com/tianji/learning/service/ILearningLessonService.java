package com.tianji.learning.service;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.po.LearningLesson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;

import java.util.List;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2026-01-29
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    void addUserLessons(Long userId, List<Long> courseIds);

    PageDTO<LearningLessonVO> queryMyLessons(PageQuery query);

    LearningLessonVO queryNowLearningLesson();

    LearningLessonVO queryLessonsByCourseId(Long courseId);

    void removeUserLessons(Long userId, List<Long> courseIds);

    Long isLessonValid(Long courseId);

    Integer countLearningLessonByCourse(Long courseId);

    LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId);

    void createLearningPlan(Long courseId, Integer freq);

    LearningPlanPageVO queryMyPlans(PageQuery query);
}
