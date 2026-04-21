package com.tianji.data.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 日报VO
 */
@Data
public class DailyReportVO {
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

    /**
     * 学习时长（分钟）
     */
    private Integer learningDuration;

    /**
     * 课程销售排行榜
     */
    private List<CourseSalesVO> courseSalesTop;

    /**
     * 课程销售VO
     */
    @Data
    public static class CourseSalesVO {
        /**
         * 课程ID
         */
        private Long courseId;

        /**
         * 课程名称
         */
        private String courseName;

        /**
         * 销售数量
         */
        private Integer salesCount;

        /**
         * 销售金额（分）
         */
        private Integer salesAmount;
    }
}
