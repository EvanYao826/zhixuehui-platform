package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/lessons")
@Api(tags = "我的课表相关接口")
@RequiredArgsConstructor
public class LearningLessonController {

    private final ILearningLessonService lessonService;

    /**
     * 分页查询我的课表
     * @param query 分页参数
     * @return 分页结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询我的课表")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        return lessonService.queryMyLessons(query);
    }

    /**
     * 查询当前正在学习的课程
     * @return 正在学习的课程
     */
    @GetMapping("/now")
    @ApiOperation("查询当前正在学习的课程")
    public LearningLessonVO queryNowLearningLesson() {
        return lessonService.queryNowLearningLesson();
    }

    /**
     * 根据课程id查询课程信息
     * @param courseId 课程id
     * @return 课程信息
     */
    @GetMapping("/{courseId}")
    @ApiOperation("根据id查询课程信息")
    public LearningLessonVO queryLearningByCourseId(@PathVariable("courseId") Long courseId) {
        return lessonService.queryLessonsByCourseId(courseId);
    }

    /**
     * 删除课程
     * @param courseId 课程id
     */
    @DeleteMapping("/{courseId}")
    @ApiOperation("根据课程Id删除课程")
    public void deleteLearningState(@PathVariable("courseId") Long courseId) {
        lessonService.removeById(courseId);
    }

    /**
     * 查询课程是否可学习
     * @param courseId 课程id
     * @return 课程是否可学习
     */
    @GetMapping("/{courseId}/valid")
    @ApiOperation("查询校验课程是否可学习")
    public Long isLessonValid(@PathVariable("courseId") Long courseId) {
        return lessonService.isLessonValid(courseId);
    }

    /**
     * 统计课程学习人数
     * @param courseId 课程id
     * @return 课程学习人数
     */
    @GetMapping("/{courseId}/count")
    @ApiOperation("统计课程学习人数")
    public Integer countLearningLessonByCourse(@PathVariable("courseId") Long courseId) {
        return lessonService.countLearningLessonByCourse(courseId);
    }

    @PostMapping("/plans")
    @ApiOperation("创建学习计划")
    public void createLearningPlans(@Valid @RequestBody LearningPlanDTO  planDTO) {
        lessonService.createLearningPlan(planDTO.getCourseId(), planDTO.getFreq());
    }


    @ApiOperation("查询我的学习计划")
    @GetMapping("/plans")
    public LearningPlanPageVO queryMyPlans(PageQuery query) {
        return lessonService.queryMyPlans(query);
    }
}
