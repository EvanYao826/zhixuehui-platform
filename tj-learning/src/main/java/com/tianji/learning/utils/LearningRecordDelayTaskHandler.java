package com.tianji.learning.utils;

import com.tianji.common.utils.JsonUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningRecordDelayTaskHandler {

    private final LearningRecordMapper recordMapper;
    private final ILearningLessonService lessonService;
    private final DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();
    private final StringRedisTemplate redisTemplate;
    private static final String RECORD_KEY_TEMPLATE = "learning:record:";

    private static volatile boolean begin = true;

    @PostConstruct
    public void init(){
        CompletableFuture.runAsync(this::handleDelayTask);
    }

    @PreDestroy
    public void destroy(){
        begin = false;
        log.debug("销毁延迟任务处理线程 LearningRecordDelayTaskHandler");
    }

    public void handleDelayTask() {
        while ( begin){
            try {
                // 1.获取到期的延迟任务
                DelayTask<RecordTaskData> task = queue.take();
                RecordTaskData data = task.getData();
                // 2.查询Redis 数据
                LearningRecord record = readRecordCache(data.getLessonId(), data.getSectionId());
                if (record == null) {
                    continue;
                }
                // 3.比较数据，moment值
                if (!Objects.equals(data.getMoment(), record.getMoment())) {
                    //  不一致，说明用户还在持续提交播放进度，放弃旧数据
                    continue;
                }
                // 4. 一致，持久化播放进度，更新数据
                // 4.1.更新学习记录的moment值
                record.setFinished(null);
                recordMapper.updateById(record);
                // 4.2.更新最近学习记录的完成状态
                LearningLesson lesson = new LearningLesson();
                lesson.setId(data.getLessonId());
                lesson.setLatestSectionId(data.getSectionId());
                lesson.setLatestLearnTime(LocalDateTime.now());
                lessonService.updateById(lesson);

            } catch (InterruptedException e) {
                log.error("延迟任务处理异常", e);
            }
        }
    }

    public void addLearningRecordTask(LearningRecord  record){
        // 1.添加数据到Redis缓存
        writeRecordCache(record);
        // 2.添加延迟任务到延迟队列DelayQueue
        queue.add(new DelayTask<>(new RecordTaskData(record), 20, TimeUnit.SECONDS));
    }

    /**
     * 更新学习记录缓存
     * @param record
     */
    public void writeRecordCache(LearningRecord record) {
        log.debug("更新学习记录缓存");
        try {
            // 1.数据转换
            String json = JsonUtils.toJsonStr(new RecordCacheData(record));
            // 2.写入缓存
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, record.getSectionId());
            redisTemplate.opsForHash().put(key, record.getSectionId().toString(),json);
            // 3.设置有效期
            redisTemplate.expire(key, Duration.ofMinutes(1));
        } catch (Exception e) {
            log.error("更新学习记录缓存失败", e);
        }

    }

    /**
     * 读取缓存数据
     * @param lessonId
     * @param sectionId
     * @return
     */
    public LearningRecord readRecordCache(Long lessonId, Long sectionId){
        try {
            // 1.读取Redis数据
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
            Object cacheData = redisTemplate.opsForHash().get(key, sectionId.toString());
            if (cacheData == null) {
                return null;
            }
            // 2.数据检查和转换
            return JsonUtils.toBean(cacheData.toString(), LearningRecord.class);
        } catch (Exception e) {
            log.error("缓存读取异常", e);
            return null;
        }
    }

    /**
     * 删除缓存数据
     * @param lessonId
     * /* @param sectionId
     * */
    public void clearRecordCache(Long lessonId, Long sectionId){
        // 1.删除Redis数据
        String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
        redisTemplate.opsForHash().delete(key, sectionId.toString());
    }

    @Data
    @NoArgsConstructor
    private static class RecordCacheData {
        private  Long id;
        private  Integer moment;
        private  Boolean finished;

        public RecordCacheData(LearningRecord record) {
            this.id = record.getId();
            this.moment = record.getMoment();
            this.finished = record.getFinished();
        }
    }

    @Data
    @NoArgsConstructor
    private static class RecordTaskData {
        private  Long lessonId;
        private  Long sectionId;
        private  Integer moment;

        public RecordTaskData(LearningRecord record) {
            this.lessonId = record.getLessonId();
            this.sectionId = record.getSectionId();
            this.moment = record.getMoment();
        }
    }
}
