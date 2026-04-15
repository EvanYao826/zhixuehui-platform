package com.tianji.ai.service.impl;

import com.tianji.ai.domain.dto.AdminAssistantDTO;
import com.tianji.api.dto.ai.CourseRecommendationDTO;
import com.tianji.ai.domain.vo.AdminAssistantVO;
import com.tianji.ai.domain.vo.CourseRecommendationVO;
import com.tianji.ai.service.IAiService;
import com.tianji.ai.utils.PromptTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class AiServiceImpl implements IAiService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        String response = callQwenApi(prompt);

        // 解析响应并构建返回对象
        CourseRecommendationVO vo = new CourseRecommendationVO();
        vo.setResponse(response);

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
        String response = callQwenApi(prompt);

        // 解析响应并构建返回对象
        AdminAssistantVO vo = new AdminAssistantVO();
        vo.setResponse(response);

        return vo;
    }

    @Override
    public String vectorSimilaritySearch(String query) {
        // 暂时返回模拟结果，后续可以集成向量数据库
        return "向量搜索功能暂未实现，查询：" + query;
    }

    /**
     * 调用千问API
     */
    private String callQwenApi(String prompt) {
        try {
            URL url = new URL(baseUrl + "/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus");
            requestBody.put("messages", new Object[]{
                    Map.of("role", "user", "content", prompt)
            });
            requestBody.put("temperature", 0.7);

            // 发送请求
            byte[] input = objectMapper.writeValueAsBytes(requestBody);
            conn.getOutputStream().write(input);

            // 读取响应
            int responseCode = conn.getResponseCode();
            System.out.println("API响应码：" + responseCode);
            
            // 读取错误流（如果有）
            if (responseCode != HttpURLConnection.HTTP_OK) {
                java.io.BufferedReader errorReader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.out.println("API错误信息：" + errorResponse.toString());
                return "API调用失败，响应码：" + responseCode + "，错误信息：" + errorResponse.toString();
            }

            java.io.BufferedReader in = new java.io.BufferedReader(
                    new java.io.InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 解析响应
            Map<String, Object> responseMap = objectMapper.readValue(response.toString(), Map.class);
            Map<String, Object> choices = ((java.util.List<Map<String, Object>>) responseMap.get("choices")).get(0);
            Map<String, Object> message = (Map<String, Object>) choices.get("message");
            return (String) message.get("content");
        } catch (IOException e) {
            e.printStackTrace();
            return "API调用异常：" + e.getMessage();
        }
    }
}