package com.tianji.ai.service;

import com.tianji.ai.domain.dto.AdminAssistantDTO;
import com.tianji.ai.domain.dto.CourseRecommendationDTO;
import com.tianji.ai.domain.vo.AdminAssistantVO;
import com.tianji.ai.domain.vo.CourseRecommendationVO;

public interface IAiService {
    /**
     * 生成课程推荐
     */
    CourseRecommendationVO generateCourseRecommendation(CourseRecommendationDTO dto);

    /**
     * 管理端智能助手
     */
    AdminAssistantVO adminAssistant(AdminAssistantDTO dto);

    /**
     * 向量相似度查询
     */
    String vectorSimilaritySearch(String query);
}