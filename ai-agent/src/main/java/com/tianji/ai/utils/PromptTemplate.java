package com.tianji.ai.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PromptTemplate {

    // 存储各种提示模板
    private static final Map<String, String> templates = new ConcurrentHashMap<>();

    static {
        // 课程推荐模板
        templates.put("course_recommendation", "你是一个专业的课程推荐助手，根据用户的学习历史和兴趣，推荐最适合的课程。\n" +
            "\n" +
            "用户信息：\n" +
            "- 学习历史：{learning_history}\n" +
            "- 兴趣标签：{interest_tags}\n" +
            "- 学习目标：{learning_goal}\n" +
            "\n" +
            "请推荐3-5门课程，并为每门课程提供：\n" +
            "1. 课程名称\n" +
            "2. 简短描述\n" +
            "3. 推荐理由\n" +
            "4. 适合度评分（1-10）\n" +
            "\n" +
            "请确保推荐的课程符合用户的学习目标和兴趣，并且内容专业、有价值。");

        // 管理端智能助手模板
        templates.put("admin_assistant", "你是一个教育平台的管理端智能助手，负责帮助管理员分析数据、生成报告和提供运营建议。\n" +
            "\n" +
            "请根据以下信息：\n" +
            "- 平台数据：{platform_data}\n" +
            "- 管理员问题：{admin_question}\n" +
            "\n" +
            "提供详细的分析和建议，包括：\n" +
            "1. 数据解读\n" +
            "2. 趋势分析\n" +
            "3. 问题解答\n" +
            "4. 具体建议\n" +
            "\n" +
            "请确保回答专业、准确、有针对性。");

        // 用户画像分析模板
        templates.put("user_profile", "你是一个用户画像分析专家，根据用户的行为数据和学习记录，分析用户的学习习惯和偏好。\n" +
            "\n" +
            "用户数据：\n" +
            "- 学习时长：{learning_duration}\n" +
            "- 完成课程：{completed_courses}\n" +
            "- 互动行为：{interaction_behavior}\n" +
            "- 搜索记录：{search_history}\n" +
            "\n" +
            "请分析用户的：\n" +
            "1. 学习风格\n" +
            "2. 兴趣偏好\n" +
            "3. 学习节奏\n" +
            "4. 潜在需求\n" +
            "5. 个性化建议\n" +
            "\n" +
            "分析要基于数据，结论要客观准确。");
    }

    /**
     * 获取提示模板
     */
    public static String getTemplate(String key) {
        return templates.get(key);
    }

    /**
     * 渲染提示模板，替换占位符
     */
    public static String renderTemplate(String key, Map<String, String> variables) {
        String template = getTemplate(key);
        if (template == null) {
            return "";
        }

        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return result;
    }

    /**
     * 添加自定义提示模板
     */
    public static void addTemplate(String key, String template) {
        templates.put(key, template);
    }

    /**
     * 删除提示模板
     */
    public static void removeTemplate(String key) {
        templates.remove(key);
    }
}