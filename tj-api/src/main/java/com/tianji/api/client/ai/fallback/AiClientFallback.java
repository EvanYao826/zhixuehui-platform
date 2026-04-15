package com.tianji.api.client.ai.fallback;

import com.tianji.api.dto.ai.CourseRecommendationDTO;
import com.tianji.api.client.ai.AiClient;
import com.tianji.common.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiClientFallback implements AiClient {

    @Override
    public R<?> getCourseRecommendation(CourseRecommendationDTO dto) {
        log.error("调用AI服务获取课程推荐失败，返回默认推荐");
        return R.ok("AI服务暂时不可用，返回默认推荐");
    }

    @Override
    public R<?> vectorSimilaritySearch(String query) {
        log.error("调用AI服务进行向量搜索失败，返回默认结果");
        return R.ok("AI服务暂时不可用，返回默认搜索结果");
    }
}
