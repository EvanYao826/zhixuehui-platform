package com.tianji.trade.service;

import com.tianji.trade.domain.vo.CouponStatsVO;

import java.time.LocalDate;

/**
 * 优惠券服务接口
 */
public interface ICouponService {

    /**
     * 获取优惠券统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 优惠券统计数据
     */
    CouponStatsVO getCouponStats(LocalDate startDate, LocalDate endDate);

    /**
     * 获取优惠券使用情况详情
     * @param couponId 优惠券ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 优惠券使用情况详情
     */
    CouponStatsVO.CouponUsageDetailVO getCouponUsageDetail(Long couponId, LocalDate startDate, LocalDate endDate);
}
