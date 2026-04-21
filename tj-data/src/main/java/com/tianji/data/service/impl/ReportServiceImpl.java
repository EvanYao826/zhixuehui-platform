package com.tianji.data.service.impl;

import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.common.utils.JsonUtils;
import com.tianji.data.constants.RedisConstants;
import com.tianji.data.model.vo.DailyReportVO;
import com.tianji.data.model.vo.WeeklyReportVO;
import com.tianji.data.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StringRedisTemplate redisTemplate;
    private final CourseClient courseClient;

    @Override
    public DailyReportVO getDailyReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        // 从Redis获取缓存数据
        String key = RedisConstants.KEY_DAILY_REPORT + date.toString();
        String cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData != null) {
            return JsonUtils.toBean(cachedData, DailyReportVO.class);
        }

        // 生成日报
        DailyReportVO report = generateDailyReportData(date);

        // 缓存到Redis
        redisTemplate.opsForValue().set(key, JsonUtils.toJsonStr(report));

        return report;
    }

    @Override
    public WeeklyReportVO getWeeklyReport(LocalDate startDate) {
        if (startDate == null) {
            // 获取本周一
            startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        }

        // 从Redis获取缓存数据
        String key = RedisConstants.KEY_WEEKLY_REPORT + startDate.toString();
        String cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData != null) {
            return JsonUtils.toBean(cachedData, WeeklyReportVO.class);
        }

        // 生成周报
        WeeklyReportVO report = generateWeeklyReportData(startDate);

        // 缓存到Redis
        redisTemplate.opsForValue().set(key, JsonUtils.toJsonStr(report));

        return report;
    }

    @Override
    public void generateDailyReport(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        generateDailyReportData(date);
    }

    @Override
    public void generateWeeklyReport(LocalDate startDate) {
        if (startDate == null) {
            // 获取本周一
            startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        }
        generateWeeklyReportData(startDate);
    }

    /**
     * 生成日报数据
     */
    private DailyReportVO generateDailyReportData(LocalDate date) {
        DailyReportVO report = new DailyReportVO();
        report.setDate(date);

        // 暂时使用模拟数据，需要集成TradeClient获取真实数据
        report.setOrderCount(10);
        report.setOrderAmount(100000); // 1000元

        // 生成课程销售排行榜
        List<DailyReportVO.CourseSalesVO> courseSalesTop = generateCourseSalesTop();
        report.setCourseSalesTop(courseSalesTop);

        // 暂时设置为0，需要其他服务支持
        report.setNewUserCount(5);
        report.setLearningCount(8);
        report.setLearningDuration(120); // 120分钟

        return report;
    }

    /**
     * 生成周报数据
     */
    private WeeklyReportVO generateWeeklyReportData(LocalDate startDate) {
        WeeklyReportVO report = new WeeklyReportVO();
        report.setStartDate(startDate);
        report.setEndDate(startDate.plusDays(6));

        List<WeeklyReportVO.DailySummaryVO> dailySummaries = new ArrayList<>();
        int totalNewUserCount = 0;
        int totalOrderCount = 0;
        int totalOrderAmount = 0;
        int totalLearningCount = 0;
        int totalLearningDuration = 0;

        // 收集每日数据
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            DailyReportVO dailyReport = getDailyReport(date);

            WeeklyReportVO.DailySummaryVO dailySummary = new WeeklyReportVO.DailySummaryVO();
            dailySummary.setDate(date);
            dailySummary.setNewUserCount(dailyReport.getNewUserCount());
            dailySummary.setOrderCount(dailyReport.getOrderCount());
            dailySummary.setOrderAmount(dailyReport.getOrderAmount());
            dailySummary.setLearningCount(dailyReport.getLearningCount());

            dailySummaries.add(dailySummary);

            // 累计周数据
            totalNewUserCount += dailyReport.getNewUserCount();
            totalOrderCount += dailyReport.getOrderCount();
            totalOrderAmount += dailyReport.getOrderAmount();
            totalLearningCount += dailyReport.getLearningCount();
            totalLearningDuration += dailyReport.getLearningDuration();
        }

        report.setDailySummaries(dailySummaries);
        report.setNewUserCount(totalNewUserCount);
        report.setOrderCount(totalOrderCount);
        report.setOrderAmount(totalOrderAmount);
        report.setLearningCount(totalLearningCount);
        report.setLearningDuration(totalLearningDuration);

        // 获取周课程销售排行榜
        List<DailyReportVO.CourseSalesVO> courseSalesTop = generateCourseSalesTop();
        report.setCourseSalesTop(courseSalesTop);

        return report;
    }

    /**
     * 生成课程销售排行榜
     */
    private List<DailyReportVO.CourseSalesVO> generateCourseSalesTop() {
        // 模拟课程销售数据
        Map<Long, Integer> courseSalesMap = new HashMap<>();
        Map<Long, Integer> courseAmountMap = new HashMap<>();

        // 模拟数据
        courseSalesMap.put(1L, 20);
        courseAmountMap.put(1L, 200000); // 2000元
        courseSalesMap.put(2L, 15);
        courseAmountMap.put(2L, 150000); // 1500元
        courseSalesMap.put(3L, 10);
        courseAmountMap.put(3L, 100000); // 1000元

        // 获取所有课程ID
        List<Long> courseIds = new ArrayList<>(courseSalesMap.keySet());
        Map<Long, String> courseNameMap = new HashMap<>();

        // 批量获取课程信息
        if (!courseIds.isEmpty()) {
            try {
                List<CourseSimpleInfoDTO> courses = courseClient.getSimpleInfoList(courseIds);
                for (CourseSimpleInfoDTO course : courses) {
                    courseNameMap.put(course.getId(), course.getName());
                }
            } catch (Exception e) {
                // 忽略异常，使用默认课程名称
            }
        }

        // 转换为销售VO并排序
        return courseSalesMap.entrySet().stream()
                .map(entry -> {
                    Long courseId = entry.getKey();

                    DailyReportVO.CourseSalesVO salesVO = new DailyReportVO.CourseSalesVO();
                    salesVO.setCourseId(courseId);
                    salesVO.setSalesCount(entry.getValue());
                    salesVO.setSalesAmount(courseAmountMap.getOrDefault(courseId, 0));

                    // 获取课程名称
                    String courseName = courseNameMap.get(courseId);
                    if (courseName != null) {
                        salesVO.setCourseName(courseName);
                    } else {
                        salesVO.setCourseName("课程" + courseId);
                    }

                    return salesVO;
                })
                .sorted((a, b) -> b.getSalesAmount() - a.getSalesAmount())
                .limit(10)
                .collect(Collectors.toList());
    }
}
