package com.tianji.learning.controller;


import com.tianji.api.dto.exam.QuestionDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.query.QuestionPageQuery;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 互动提问的问题表 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2026-03-13
 */
@RestController
@RequestMapping("/admin/questions")
@Api(tags = "互动提问-问题管理接口")
@RequiredArgsConstructor
public class InteractionQuestionAdminController {

    private final IInteractionQuestionService questionService;

    @GetMapping("page")
    @ApiOperation("管理端分页查询问题列表")
    public PageDTO<QuestionAdminVO> queryQuestionsPageAdmin(QuestionAdminPageQuery query){
        return questionService.queryQuestionPageAdmin(query);
    }

    @PutMapping("/{id}/hidden/{hidden}")
    @ApiOperation("修改问题隐藏状态")
    public void updateQuestionHidden(@ApiParam(value = "问题id", example = "1") @PathVariable("id")  Long id,
                                     @ApiParam(value = "隐藏状态", example = "true") @PathVariable("hidden")  Boolean hidden){
        questionService.updateQuestionHidden(id, hidden);
    }


}
