package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.cache.CategoryCache;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CategoryClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.client.search.SearchClient;
import com.tianji.api.client.user.UserClient;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.api.dto.exam.QuestionDTO;
import com.tianji.api.dto.user.UserDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.tianji.learning.domain.po.InteractionReply;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.mapper.InteractionQuestionMapper;
import com.tianji.learning.query.QuestionPageQuery;
import com.tianji.learning.service.IInteractionQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.learning.service.IInteractionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 互动提问的问题表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2026-03-13
 */
@Service
@RequiredArgsConstructor
public class InteractionQuestionServiceImpl extends ServiceImpl<InteractionQuestionMapper, InteractionQuestion> implements IInteractionQuestionService {

    private final IInteractionReplyService replyService;
    private final UserClient userClient;
    private final SearchClient searchClient;
    private final CourseClient courseClient;
    private final CatalogueClient catalogueClient;
    private final CategoryCache categoryCache;
    /**
     * 保存互动问题
     *
     * @param questionFormDTO 问题信息
     */
    @Override
    public void saveQuestion(QuestionFormDTO questionFormDTO) {
        // 1.获取当前用户id
        Long userId = UserContext.getUser();
        // 2.数据封装
        InteractionQuestion question = BeanUtils.copyBean(questionFormDTO, InteractionQuestion.class)
                .setUserId(userId);
        // 3.保存数据写入数据库
        save(question);
    }

    /**
     * 分页查询问题列表
     *
     * @param query 查询参数
     * @return 问题列表
     */
    @Override
    public PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query) {
        // 1.参数校验，课程id和小节id不能同时为空
        Long courseId = query.getCourseId();
        Long sectionId = query.getSectionId();
        if (courseId == null && sectionId == null) {
            throw new BadRequestException("课程id和小节id不能同时为空");
        }
        // 2. 分页查询
        // 【修复点】处理 query.getOnlyMine() 可能为 null 的情况，避免自动拆箱 NPE
        Boolean onlyMine = query.getOnlyMine();
        boolean isOnlyMine = (onlyMine != null && onlyMine);

        Page<InteractionQuestion> page = lambdaQuery()
                // 修正 select 写法，移除多余的 class 参数（视具体 MP 版本而定，标准写法通常不需要）
                .select(InteractionQuestion.class, info -> !info.getProperty().equals("description"))
                .eq(isOnlyMine, InteractionQuestion::getUserId, UserContext.getUser())
                .eq(courseId != null, InteractionQuestion::getCourseId, courseId)
                .eq(sectionId != null, InteractionQuestion::getSectionId, sectionId)
                .eq(InteractionQuestion::getHidden, false)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());

        List<InteractionQuestion> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        // 3.根据id查询提问者和最近一次回复的信息
        Set<Long> userIds = new HashSet<>();
        Set<Long> answerIds = new HashSet<>();
        // 3.1 得到问题当中的提问者id和最近一次回复的id
        for (InteractionQuestion q : records) {
            if (!q.getAnonymity()) { // 不匿名
                userIds.add(q.getUserId());
            }
            answerIds.add(q.getLatestAnswerId());
        }
        // 3.2 根据id查询最近一次回复者信息
        answerIds.remove(null);
        Map<Long, InteractionReply> replyMap = new HashMap<>(answerIds.size());
        if (CollUtils.isNotEmpty(answerIds)) {
            List<InteractionReply> replies = replyService.listByIds(answerIds);
            for (InteractionReply r : replies) {
                replyMap.put(r.getId(), r);
                if (!r.getAnonymity()) { // 不匿名
                    userIds.add(r.getUserId());
                }
            }
        }
        // 3.3 根据id查询提问者信息（提问者）
        userIds.remove(null);
        Map<Long, UserDTO> userMap = new HashMap<>(userIds.size());
        if (CollUtils.isNotEmpty(userIds)) {
            List<UserDTO> users = userClient.queryUserByIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(UserDTO::getId, u -> u));
        }

        // 4.封装结果
        List<QuestionVO> voList = new ArrayList<>(records.size());
        for (InteractionQuestion q : records) {
            // 4.1 将PO转换成VO
            QuestionVO vo = BeanUtils.copyBean(q, QuestionVO.class);
            voList.add(vo);
            // 4.2 封装提问用户信息
            if (!q.getAnonymity()) {
                UserDTO u = userMap.get(q.getUserId());
                if (u != null) {
                    vo.setUserName(u.getName());
                    vo.setUserIcon(u.getIcon());
                }
            }
            // 4.3 封装最近一次回复信息
            InteractionReply r = replyMap.get(q.getLatestAnswerId());
            if (r != null) {
                vo.setLatestReplyContent(r.getContent());
                if (!r.getAnonymity()) {
                    UserDTO user = userMap.get(r.getUserId());
                    vo.setLatestReplyUser(user.getName());
                }
            }
        }

        return PageDTO.of(page, voList);
    }

    /**
     * 修改问题
     *
     * @param id          问题 id
     * @param questionFormDTO 问题信息
     */
    @Override
    public void updateQuestion(Long id, QuestionFormDTO questionFormDTO) {
        // 1.获取用户 id
        Long userId = UserContext.getUser();
        // 2.数据校验
        InteractionQuestion question = lambdaQuery()
                .eq(InteractionQuestion::getId, id)
                .eq(InteractionQuestion::getUserId, userId)
                .one();
        if (question == null) {
            throw new BadRequestException("问题不存在");
        }
        // 3.数据封装 - 修复 copyBean 用法错误
        BeanUtils.copyProperties(questionFormDTO, question);
        question.setId(id)
                .setUserId(userId);
        // 4.保存更新
        saveOrUpdate(question);
    }

    /**
     * 根据 id 查询问题
     *
     * @param id 问题 id
     * @return 问题信息
     */
    @Override
    public QuestionVO getQuestionById(Long id) {
        // 1.根据id查询数据
        InteractionQuestion question = getById(id);
        // 2.数据校验
        if(question == null || question.getHidden()){
            // 没有数据或者是被隐藏了
            return null;
        }
        // 3.查询提问者信息
        UserDTO user = null;
        if(!question.getAnonymity()){
            user = userClient.queryUserById(question.getUserId());
        }
        // 4.封装VO
        QuestionVO vo = BeanUtils.copyBean(question, QuestionVO.class);
        if (user != null) {
            vo.setUserName(user.getName());
            vo.setUserIcon(user.getIcon());
        }
        return vo;
    }

    @Override
    public PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query) {
        // 1.处理课程名称，得到课程id
        List<Long> courseIds = null;
        if (StringUtils.isNotBlank(query.getCourseName())) {
            courseIds = searchClient.queryCoursesIdByName(query.getCourseName());
            if (CollUtils.isEmpty(courseIds)) {
                return PageDTO.empty(0L, 0L);
            }
        }
        // 2.分页查询
        Integer status = query.getStatus();
        LocalDateTime begin = query.getBeginTime();
        LocalDateTime end = query.getEndTime();
        Page<InteractionQuestion> page = lambdaQuery()
                .in(courseIds != null, InteractionQuestion::getCourseId, courseIds)
                .eq(status != null, InteractionQuestion::getStatus, status)
                .gt(begin != null, InteractionQuestion::getCreateTime, begin)
                .lt(end != null, InteractionQuestion::getCreateTime, end)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        List<InteractionQuestion> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3.准备VO需要的数据：用户数据、课程数据、章节数据
        Set<Long> userIds = new HashSet<>();
        Set<Long> cIds = new HashSet<>();
        Set<Long> cataIds = new HashSet<>();
        // 3.1.获取各种数据的id集合
        for (InteractionQuestion q : records) {
            userIds.add(q.getUserId());
            cIds.add(q.getCourseId());
            cataIds.add(q.getChapterId());
            cataIds.add(q.getSectionId());
        }
        // 3.2.根据id查询用户
        List<UserDTO> users = userClient.queryUserByIds(userIds);
        Map<Long, UserDTO> userMap = new HashMap<>(users.size());
        if (CollUtils.isNotEmpty(users)) {
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        }

        // 3.3.根据id查询课程
        List<CourseSimpleInfoDTO> cInfos = courseClient.getSimpleInfoList(cIds);
        Map<Long, CourseSimpleInfoDTO> cInfoMap = new HashMap<>(cInfos.size());
        if (CollUtils.isNotEmpty(cInfos)) {
            cInfoMap = cInfos.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        }

        // 3.4.根据id查询章节
        List<CataSimpleInfoDTO> catas = catalogueClient.batchQueryCatalogue(cataIds);
        Map<Long, String> cataMap = new HashMap<>(catas.size());
        if (CollUtils.isNotEmpty(catas)) {
            cataMap = catas.stream()
                    .collect(Collectors.toMap(CataSimpleInfoDTO::getId, CataSimpleInfoDTO::getName));
        }


        // 4.封装VO
        List<QuestionAdminVO> voList = new ArrayList<>(records.size());
        for (InteractionQuestion q : records) {
            // 4.1.将PO转VO，属性拷贝
            QuestionAdminVO vo = BeanUtils.copyBean(q, QuestionAdminVO.class);
            voList.add(vo);
            // 4.2.用户信息
            UserDTO user = userMap.get(q.getUserId());
            if (user != null) {
                vo.setUserName(user.getName());
            }
            // 4.3.课程信息以及分类信息
            CourseSimpleInfoDTO cInfo = cInfoMap.get(q.getCourseId());
            if (cInfo != null) {
                vo.setCourseName(cInfo.getName());
                vo.setCategoryName(categoryCache.getCategoryNames(cInfo.getCategoryIds()));
            }
            // 4.4.章节信息
            vo.setChapterName(cataMap.getOrDefault(q.getChapterId(), ""));
            vo.setSectionName(cataMap.getOrDefault(q.getSectionId(), ""));
        }
        return PageDTO.of(page, voList);
    }

    /**
     * 修改问题隐藏状态
     *
     * @param id     问题 id
     * @param hidden 隐藏状态
     */
    @Override
    public void updateQuestionHidden(Long id, Boolean hidden) {
        // 1. 参数校验
        if (id == null || hidden == null) {
            throw new BadRequestException("参数错误");
        }

        // 2. 查询问题是否存在
        // 注意：管理端操作通常不需要校验 userId，但必须确保数据存在
        InteractionQuestion question = lambdaQuery()
                .eq(InteractionQuestion::getId, id)
                .one();

        if (question == null) {
            throw new BadRequestException("问题不存在");
        }

        // 3. 如果当前状态与目标状态一致，无需更新
        if (question.getHidden().equals(hidden)) {
            return;
        }

        // 4. 执行更新操作
        boolean updated = lambdaUpdate()
                .eq(InteractionQuestion::getId, id)
                .set(InteractionQuestion::getHidden, hidden)
                .update();

        if (!updated) {
            throw new BadRequestException("更新失败");
        }

        // 5. 【核心业务】如果隐藏问题，则将该问题下的所有回答也一并隐藏
        if (hidden) {
            replyService.lambdaUpdate()
                    .eq(InteractionReply::getQuestionId, id)
                    .set(InteractionReply::getHidden, true)
                    .update();
        }
        // 注意：如果取消隐藏 (hidden=false)，通常不需要自动取消回答的隐藏，
        // 因为回答可能因其他原因（如违规）被单独隐藏，需由管理员手动处理或遵循特定业务规则。
        // 若业务要求取消隐藏问题时也恢复回答显示，可在此处添加 else 逻辑。
    }

}
