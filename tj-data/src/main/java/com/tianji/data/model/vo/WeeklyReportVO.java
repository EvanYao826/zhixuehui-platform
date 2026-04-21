package com.tianji.data.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 周报VO
 */
@Data
public class WeeklyReportVO {
    /**
     * 周的开始日期
     */
    private LocalDate startDate;

    /**
     * 周的结束日期
     */
    private LocalDate endDate;

    /**
     * 新增用户数
     */
    private Integer newUserCount;

    /**
     * 订单总数
     */
    private Integer orderCount;

    /**
     * 订单总金额（分）
     */
    private Integer orderAmount;

    /**
     * 学习人数
     */
    private Integer learningCount;

    /**
     * 学习时长（分钟）
     */
    private Integer learningDuration;

    /**
     * 每日数据
     */
    private List<DailySummaryVO> dailySummaries;

    /**
     * 课程销售排行榜
     */
    private List<DailyReportVO.CourseSalesVO> courseSalesTop;

    /**
     * 每日摘要VO
     */
    @Data
    public static class DailySummaryVO {
        /**
         * 日期
         */
        private LocalDate date;

        /**
         * 新增用户数
         */
        private Integer newUserCount;

        /**
         * 订单总数
         */
        private Integer orderCount;

        /**
         * 订单总金额（分）
         */
        private Integer orderAmount;

        /**
         * 学习人数
         */
        private Integer learningCount;
    }
}
