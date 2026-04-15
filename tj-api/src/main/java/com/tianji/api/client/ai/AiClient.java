package com.tianji.api.client.ai;

import com.tianji.api.client.ai.fallback.AiClientFallback;
import com.tianji.api.dto.ai.CourseRecommendationDTO;
import com.tianji.common.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ai-agent", fallback = AiClientFallback.class)
public interface AiClient {

    @PostMapping("/ai/course/recommendation")
    R<?> getCourseRecommendation(@RequestBody CourseRecommendationDTO dto);

    @GetMapping("/ai/vector/search")
    R<?> vectorSimilaritySearch(@RequestParam("query") String query);
}
