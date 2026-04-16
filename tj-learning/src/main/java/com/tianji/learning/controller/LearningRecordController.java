package com.tianji.learning.controller;


import com.tianji.api.client.learning.LearningClient;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.dto.LearningRecordFormDTO;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2026-01-30
 */
@RestController
@RequestMapping("/learning-records")
@Api(tags = "学习记录接口")
@RequiredArgsConstructor
public class LearningRecordController {

    private final ILearningRecordService recordService;
    private final LearningClient lessonService;

    /**
     * 查询用户某门课程的进度信息
     * @param courseId 课程id
     * @return 进度信息
     */
    @GetMapping("/course/{courseId}")
    @ApiOperation("查询用户某门课程的进度信息")
    public LearningLessonDTO queryLearningRecordByCourse(
            @ApiParam(value = "课程id", example = "1") @PathVariable("courseId") Long courseId){
        return recordService.queryLearningRecordByCourse(courseId);
    }

    /**
     * 添加学习记录
     * @param recordDto 学习记录
     */
    @PostMapping()
    @ApiOperation("添加学习记录")
    public void addLearningRecord(
            @ApiParam(value = "学习记录", required = true) @RequestBody LearningRecordFormDTO recordDto){
        recordService.addLearningRecord(recordDto);
    }

    /**
     * 查询用户所有学习记录
     * @param userId 用户id
     * @return 学习记录列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("查询用户所有学习记录")
    public List<LearningRecordDTO> queryLearningRecordsByUser(
            @ApiParam(value = "用户id", example = "1") @PathVariable("userId") Long userId){
        return recordService.queryLearningRecordsByUser(userId);
    }

}
