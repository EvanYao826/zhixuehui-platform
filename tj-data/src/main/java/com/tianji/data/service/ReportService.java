package com.tianji.data.service;

import com.tianji.data.model.vo.DailyReportVO;
import com.tianji.data.model.vo.WeeklyReportVO;

import java.time.LocalDate;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 获取日报
     * @param date 日期，为空则获取当天
     * @return 日报数据
     */
    DailyReportVO getDailyReport(LocalDate date);

    /**
     * 获取周报
     * @param startDate 周的开始日期，为空则获取本周
     * @return 周报数据
     */
    WeeklyReportVO getWeeklyReport(LocalDate startDate);

    /**
     * 生成日报
     * @param date 日期，为空则生成当天
     */
    void generateDailyReport(LocalDate date);

    /**
     * 生成周报
     * @param startDate 周的开始日期，为空则生成本周
     */
    void generateWeeklyReport(LocalDate startDate);
}
