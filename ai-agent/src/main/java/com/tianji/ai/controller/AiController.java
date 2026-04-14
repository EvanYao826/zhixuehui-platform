package com.tianji.ai.controller;

import com.tianji.ai.domain.dto.AdminAssistantDTO;
import com.tianji.ai.domain.dto.CourseRecommendationDTO;
import com.tianji.ai.domain.vo.AdminAssistantVO;
import com.tianji.ai.domain.vo.CourseRecommendationVO;
import com.tianji.ai.service.IAiService;
import com.tianji.common.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@Api(tags = "AI服务接口")
public class AiController {

    @Autowired
    private IAiService aiService;

    @PostMapping("/course/recommendation")
    @ApiOperation("课程推荐")
    public R<CourseRecommendationVO> getCourseRecommendation(@RequestBody CourseRecommendationDTO dto) {
        CourseRecommendationVO result = aiService.generateCourseRecommendation(dto);
        return R.ok(result);
    }

    @PostMapping("/admin/assistant")
    @ApiOperation("管理端智能助手")
    public R<AdminAssistantVO> adminAssistant(@RequestBody AdminAssistantDTO dto) {
        AdminAssistantVO result = aiService.adminAssistant(dto);
        return R.ok(result);
    }

    @GetMapping("/vector/search")
    @ApiOperation("向量相似度搜索")
    public R<String> vectorSimilaritySearch(@RequestParam String query) {
        String result = aiService.vectorSimilaritySearch(query);
        return R.ok(result);
    }
}