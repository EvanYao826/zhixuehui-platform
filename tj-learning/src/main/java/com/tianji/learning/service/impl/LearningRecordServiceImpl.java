package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.exceptions.DbException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.LearningRecordFormDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.SectionType;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.tianji.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.learning.utils.LearningRecordDelayTaskHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 学习记录表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2026-01-30
 */
@Service
@RequiredArgsConstructor
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    private final ILearningLessonService lessonService;

    private final CourseClient courseClient;

    private final LearningRecordDelayTaskHandler taskHandler;

    /**
     * 查询用户某门课程的进度信息
     * @param courseId 课程id
     * @return 进度信息
     */
    @Override
    public LearningLessonDTO queryLearningRecordByCourse(Long courseId) {

        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.查询课表
        LearningLesson lesson = lessonService.queryByUserIdAndCourseId(userId,courseId);
        if (lesson == null) {
            return null;
        }
        // 3.查询学习记录
        List<LearningRecord> records = lambdaQuery()
                .eq(LearningRecord::getLessonId, lesson.getId())
                .list();
         List<LearningRecordDTO> list = BeanUtils.copyList(records, LearningRecordDTO.class);
        // 4.封装数据
        LearningLessonDTO lessonDTO = new LearningLessonDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setLatestSectionId(lesson.getLatestSectionId());
        lessonDTO.setRecords(list);
        return lessonDTO;
    }

    /**
     * 添加学习记录
     * @param recordDto 学习记录
     */
    @Override
    public void addLearningRecord(LearningRecordFormDTO recordDto) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();

        // 2.处理学习记录
        boolean finished = false;
        if (recordDto.getSectionType() == SectionType.VIDEO) {
            // 2.1处理视频
            finished = handleVideoRecord(userId, recordDto);
        }else {
            // 2.2处理考试
            finished = handleExamRecord(userId, recordDto);
        }
        if (!finished) {
            // 没有新学完的小结，无需更新课表中的进度
            return;
        }
        // 3.处理课表数据
        handleLearningLessonsChanges(recordDto);
    }


    /**
     * 处理学习记录
     * @param recordDto 学习记录
     */
    private void handleLearningLessonsChanges(LearningRecordFormDTO recordDto) {
        // 1.查询课表
        LearningLesson lesson = lessonService.getById(recordDto.getLessonId());
        if (lesson == null){
            throw new BizIllegalException("课程不存在，查询课表失败！！");
        }
        // 2.判断是否有新地完成小节
        boolean allFinished = false;

            // 3.如果有新完成小节，则需要查询课程数据
            CourseFullInfoDTO course = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
            if (course == null){
                throw new BizIllegalException("课程不存在，查询课程数据失败！！");
            }
            // 4.比较课程是否全部学完：已学习节数 >= 课程总节数
            allFinished  = lesson.getLearnedSections() +1 >= course.getSectionNum();

        // 5.更新课表数据
        lessonService.lambdaUpdate()
                .set(lesson.getLearnedSections() == 0, LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .set(allFinished, LearningLesson::getPlanStatus, LessonStatus.FINISHED.getValue())
                .setSql("learned_sections = learned_sections + 1")
                .eq(LearningLesson::getId, lesson.getId())
                .update();
    }

    /**
     * 处理课表数据
     * @param recordDto 学习记录
     
     */
    private boolean handleVideoRecord(Long userId, LearningRecordFormDTO recordDto) {
        // 1.查询旧地学习记录
        LearningRecord oldRecord = queryOldRecord(recordDto.getLessonId(), recordDto.getSectionId());
        // 2.判断是否存在
        if (oldRecord == null) {
            // 3.不存在，则新增
            // 3.1.转换DTO为 PO
            LearningRecord record = BeanUtils.toBean(recordDto, LearningRecord.class);
            // 3.2.填充数据
            record.setUserId(userId);
            // 3.3.保存
            boolean save = save(record);
            if (! save){
                throw new DbException("新增考试记录失败！！");
            }
            return false;
        }

        // 4.存在，则更新
        // 4.1.判断是否是第一次完成
        boolean finished = !oldRecord.getFinished() && recordDto.getMoment()*2 >= oldRecord.getMoment();
        if (!finished){
            LearningRecord record = BeanUtils.toBean(recordDto, LearningRecord.class);
            taskHandler.addLearningRecordTask(record);
            return false;
        }
        // 4.2.更新数据
        boolean success = lambdaUpdate()
                .eq(LearningRecord::getId, oldRecord.getId())
                .set(LearningRecord::getMoment, recordDto.getMoment())
                .set(LearningRecord::getFinished, true)
                .set(LearningRecord::getFinishTime, recordDto.getCommitTime())
                .update();
        if(!success){
            throw new DbException("更新学习记录失败！！");
        }
        // 4.3 清理缓存
        taskHandler.clearRecordCache(recordDto.getLessonId(), recordDto.getSectionId());
        return true;
    }

    /**
     * 查询旧学习记录
     * @param lessonId 课程id
     * @param sectionId 小节id
     * @return 旧学习记录
     */
    private LearningRecord queryOldRecord(Long lessonId, Long sectionId) {
        // 1.查询缓存
        LearningRecord record = taskHandler.readRecordCache(lessonId, sectionId);
        // 2.如果命中，则返回缓存数据
        if (record != null){
            return record;
        }
        // 3.如果没有命中，则查询数据库
        record = lambdaQuery()
                .eq(LearningRecord::getLessonId, lessonId)
                .eq(LearningRecord::getSectionId, sectionId)
                .one();
        // 4.写入缓存
        taskHandler.writeRecordCache(record);
        return record;
    }

    /**
     * 处理考试记录
     * @param userId 用户id
     * @param recordDto 学习记录
     * @return 是否完成
     */
    private boolean handleExamRecord(Long userId, LearningRecordFormDTO recordDto) {
        // 1.转换DTO为 PO
        LearningRecord record = BeanUtils.toBean(recordDto, LearningRecord.class);
        // 2.填充数据
        record.setUserId(userId);
        record.setFinished(true);
        record.setFinishTime(recordDto.getCommitTime());
        // 3.保存
        boolean save = save(record);
        if (! save){
            throw new DbException("新增考试记录失败！！");
        }
        return true;
    }
}
