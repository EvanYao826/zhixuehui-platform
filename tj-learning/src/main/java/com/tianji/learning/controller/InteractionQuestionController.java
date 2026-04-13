package com.tianji.learning.controller;


import com.tianji.api.dto.exam.QuestionDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.query.QuestionPageQuery;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
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
@RequestMapping("/questions")
@Api(tags = "互动提问-问题管理接口")
@RequiredArgsConstructor
public class InteractionQuestionController {

    private final IInteractionQuestionService questionService;

    @ApiOperation("新增互动问题")
    @PostMapping
    public void saveQuestion(@Valid @RequestBody QuestionFormDTO questionFromDTO){
        questionService.saveQuestion(questionFromDTO);
    }

    @PutMapping("/questions/{id}")
    @ApiOperation("修改互动问题")
    public void updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionFormDTO questionFormDTO){
        questionService.updateQuestion(id, questionFormDTO);
    }
    @GetMapping("page")
    @ApiOperation("分页查询问题列表")
    public PageDTO<QuestionVO> queryQuestionsPage(QuestionPageQuery query){
        return questionService.queryQuestionPage(query);
    }

    @ApiOperation("根据id查询问题详情")
    @GetMapping("/{id}")
    public QuestionVO queryQuestionById(@ApiParam(value = "问题id", example = "1") @PathVariable("id")  Long id){
        return questionService.getQuestionById(id);
    }

    @ApiOperation("根据id删除问题")
    @DeleteMapping("/{id}")
    public void deleteQuestionById(@ApiParam(value = "问题id", example = "1") @PathVariable("id")  Long id){
        questionService.removeById(id);
    }
}
