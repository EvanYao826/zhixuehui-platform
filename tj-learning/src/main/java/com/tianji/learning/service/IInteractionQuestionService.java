package com.tianji.learning.service;

import com.tianji.api.dto.exam.QuestionDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.query.QuestionPageQuery;

/**
 * <p>
 * 互动提问的问题表 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2026-03-13
 */
public interface IInteractionQuestionService extends IService<InteractionQuestion> {

    void saveQuestion(QuestionFormDTO questionFormDTO);

    PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query);

    void updateQuestion(Long id, QuestionFormDTO questionFormDTO);

    QuestionVO getQuestionById(Long id);

    PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query);

    void updateQuestionHidden(Long id, Boolean hidden);
}
