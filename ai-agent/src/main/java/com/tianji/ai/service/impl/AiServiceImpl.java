package com.tianji.ai.service.impl;

import com.tianji.ai.domain.dto.AdminAssistantDTO;
import com.tianji.ai.domain.dto.CourseRecommendationDTO;
import com.tianji.ai.domain.vo.AdminAssistantVO;
import com.tianji.ai.domain.vo.CourseRecommendationVO;
import com.tianji.ai.service.IAiService;
import com.tianji.ai.utils.PromptTemplate;
import org.springframework.ai.elasticsearch.ElasticsearchDocumentStore;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiServiceImpl implements IAiService {

    @Autowired
    private OpenAiChatClient chatClient;

    @Autowired
    private ElasticsearchDocumentStore documentStore;

    @Override
    public CourseRecommendationVO generateCourseRecommendation(CourseRecommendationDTO dto) {
        // 构建提示变量
        Map<String, String> variables = new HashMap<>();
        variables.put("learning_history", String.join(", ", dto.getLearningHistory()));
        variables.put("interest_tags", String.join(", ", dto.getInterestTags()));
        variables.put("learning_goal", dto.getLearningGoal());

        // 渲染提示模板
        String prompt = PromptTemplate.renderTemplate("course_recommendation", variables);

        // 调用AI生成推荐
        String response = chatClient.call(prompt);

        // 解析响应并构建返回对象
        CourseRecommendationVO vo = new CourseRecommendationVO();
        // 这里简化处理，实际需要解析AI的响应
        // 示例解析逻辑
        // vo.setCourses(parseCourseRecommendations(response));

        return vo;
    }

    @Override
    public AdminAssistantVO adminAssistant(AdminAssistantDTO dto) {
        // 构建提示变量
        Map<String, String> variables = new HashMap<>();
        variables.put("platform_data", dto.getPlatformData().toString());
        variables.put("admin_question", dto.getAdminQuestion());

        // 渲染提示模板
        String prompt = PromptTemplate.renderTemplate("admin_assistant", variables);

        // 调用AI生成响应
        String response = chatClient.call(prompt);

        // 解析响应并构建返回对象
        AdminAssistantVO vo = new AdminAssistantVO();
        // 这里简化处理，实际需要解析AI的响应
        // 示例解析逻辑
        // vo.setDataAnalysis(...);
        // vo.setTrendAnalysis(...);
        // vo.setQuestionAnswer(...);
        // vo.setSuggestions(...);

        return vo;
    }

    @Override
    public String vectorSimilaritySearch(String query) {
        // 使用Elasticsearch进行向量相似度搜索
        List<String> results = documentStore.similaritySearch(query, 5);
        return String.join("\n", results);
    }

    // 辅助方法：解析课程推荐响应
    private List<CourseRecommendationVO.RecommendedCourse> parseCourseRecommendations(String response) {
        // 实际实现需要根据AI的响应格式进行解析
        return null;
    }
}