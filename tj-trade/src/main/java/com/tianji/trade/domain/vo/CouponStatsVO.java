package com.tianji.trade.domain.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 优惠券统计VO
 */
@Data
public class CouponStatsVO {
    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 总发放数量
     */
    private Integer totalIssued;

    /**
     * 总使用数量
     */
    private Integer totalUsed;

    /**
     * 总过期数量
     */
    private Integer totalExpired;

    /**
     * 总使用金额（分）
     */
    private Integer totalUsedAmount;

    /**
     * 优惠券使用情况列表
     */
    private List<CouponUsageVO> usageList;

    /**
     * 优惠券使用情况
     */
    @Data
    public static class CouponUsageVO {
        /**
         * 优惠券ID
         */
        private Long couponId;

        /**
         * 优惠券名称
         */
        private String couponName;

        /**
         * 发放数量
         */
        private Integer issuedCount;

        /**
         * 使用数量
         */
        private Integer usedCount;

        /**
         * 过期数量
         */
        private Integer expiredCount;

        /**
         * 使用金额（分）
         */
        private Integer usedAmount;

        /**
         * 使用率
         */
        private Double usageRate;
    }

    /**
     * 优惠券使用详情VO
     */
    @Data
    public static class CouponUsageDetailVO {
        /**
         * 优惠券ID
         */
        private Long couponId;

        /**
         * 优惠券名称
         */
        private String couponName;

        /**
         * 发放数量
         */
        private Integer issuedCount;

        /**
         * 使用数量
         */
        private Integer usedCount;

        /**
         * 过期数量
         */
        private Integer expiredCount;

        /**
         * 使用金额（分）
         */
        private Integer usedAmount;

        /**
         * 使用率
         */
        private Double usageRate;

        /**
         * 每日使用情况
         */
        private List<DailyUsageVO> dailyUsageList;

        /**
         * 每日使用情况
         */
        @Data
        public static class DailyUsageVO {
            /**
             * 日期
             */
            private LocalDate date;

            /**
             * 使用数量
             */
            private Integer usedCount;

            /**
             * 使用金额（分）
             */
            private Integer usedAmount;
        }
    }
}
