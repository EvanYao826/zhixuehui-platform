package com.tianji.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tianji.trade.domain.po.Order;
import com.tianji.trade.domain.vo.CouponStatsVO;
import com.tianji.trade.mapper.OrderMapper;
import com.tianji.trade.service.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements ICouponService {

    private final OrderMapper orderMapper;

    @Override
    public CouponStatsVO getCouponStats(LocalDate startDate, LocalDate endDate) {
        // 设置默认日期范围
        if (startDate == null) {
            // 默认开始日期为当月第一天
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        }
        if (endDate == null) {
            // 默认结束日期为当天
            endDate = LocalDate.now();
        }

        CouponStatsVO statsVO = new CouponStatsVO();
        statsVO.setStartDate(startDate);
        statsVO.setEndDate(endDate);

        // 查询日期范围内的订单
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, startDateTime)
                .lt(Order::getCreateTime, endDateTime)
                .isNotNull(Order::getCouponIds));

        // 过滤掉couponIds为空的订单
        orders = orders.stream()
                .filter(order -> order.getCouponIds() != null && !order.getCouponIds().isEmpty())
                .collect(Collectors.toList());

        // 统计优惠券使用情况
        Map<Long, CouponStatsVO.CouponUsageVO> couponUsageMap = new HashMap<>();
        int totalIssued = 0;
        int totalUsed = 0;
        int totalExpired = 0; // 暂时设为0，需要优惠券表才能统计
        int totalUsedAmount = 0;

        for (Order order : orders) {
            // 计算订单中使用的优惠券金额（简化计算，按订单优惠金额平均分配）
            int orderDiscount = order.getDiscountAmount() != null ? order.getDiscountAmount() : 0;
            List<Long> couponIds = order.getCouponIds();
            if (couponIds != null && !couponIds.isEmpty()) {
                int couponCount = couponIds.size();
                int perCouponAmount = orderDiscount / couponCount;

                for (Long couponId : couponIds) {
                    CouponStatsVO.CouponUsageVO usageVO = couponUsageMap.computeIfAbsent(couponId, k -> {
                        CouponStatsVO.CouponUsageVO newVO = new CouponStatsVO.CouponUsageVO();
                        newVO.setCouponId(k);
                        newVO.setCouponName("优惠券" + k); // 临时名称，实际应从优惠券表获取
                        newVO.setIssuedCount(0);
                        newVO.setUsedCount(0);
                        newVO.setExpiredCount(0);
                        newVO.setUsedAmount(0);
                        return newVO;
                    });

                    usageVO.setIssuedCount(usageVO.getIssuedCount() + 1);
                    usageVO.setUsedCount(usageVO.getUsedCount() + 1);
                    usageVO.setUsedAmount(usageVO.getUsedAmount() + perCouponAmount);

                    totalIssued++;
                    totalUsed++;
                    totalUsedAmount += perCouponAmount;
                }
            }
        }

        // 计算使用率
        for (CouponStatsVO.CouponUsageVO usageVO : couponUsageMap.values()) {
            if (usageVO.getIssuedCount() > 0) {
                usageVO.setUsageRate((double) usageVO.getUsedCount() / usageVO.getIssuedCount());
            }
        }

        statsVO.setTotalIssued(totalIssued);
        statsVO.setTotalUsed(totalUsed);
        statsVO.setTotalExpired(totalExpired);
        statsVO.setTotalUsedAmount(totalUsedAmount);
        statsVO.setUsageList(new ArrayList<>(couponUsageMap.values()));

        return statsVO;
    }

    @Override
    public CouponStatsVO.CouponUsageDetailVO getCouponUsageDetail(Long couponId, LocalDate startDate, LocalDate endDate) {
        // 设置默认日期范围
        if (startDate == null) {
            // 默认开始日期为当月第一天
            startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        }
        if (endDate == null) {
            // 默认结束日期为当天
            endDate = LocalDate.now();
        }

        CouponStatsVO.CouponUsageDetailVO detailVO = new CouponStatsVO.CouponUsageDetailVO();
        detailVO.setCouponId(couponId);
        detailVO.setCouponName("优惠券" + couponId); // 临时名称，实际应从优惠券表获取

        // 查询日期范围内的订单
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .ge(Order::getCreateTime, startDateTime)
                .lt(Order::getCreateTime, endDateTime)
                .isNotNull(Order::getCouponIds));

        // 过滤掉couponIds为空的订单
        orders = orders.stream()
                .filter(order -> order.getCouponIds() != null && !order.getCouponIds().isEmpty())
                .collect(Collectors.toList());

        // 统计优惠券使用情况
        int issuedCount = 0;
        int usedCount = 0;
        int expiredCount = 0; // 暂时设为0，需要优惠券表才能统计
        int usedAmount = 0;
        Map<LocalDate, CouponStatsVO.CouponUsageDetailVO.DailyUsageVO> dailyUsageMap = new HashMap<>();

        for (Order order : orders) {
            List<Long> couponIds = order.getCouponIds();
            if (couponIds != null && couponIds.contains(couponId)) {
                // 计算订单中使用的优惠券金额（简化计算，按订单优惠金额平均分配）
                int orderDiscount = order.getDiscountAmount() != null ? order.getDiscountAmount() : 0;
                int perCouponAmount = orderDiscount / couponIds.size();

                issuedCount++;
                usedCount++;
                usedAmount += perCouponAmount;

                // 统计每日使用情况
                LocalDate orderDate = order.getCreateTime().toLocalDate();
                CouponStatsVO.CouponUsageDetailVO.DailyUsageVO dailyUsage = dailyUsageMap.computeIfAbsent(orderDate, k -> {
                    CouponStatsVO.CouponUsageDetailVO.DailyUsageVO newDaily = new CouponStatsVO.CouponUsageDetailVO.DailyUsageVO();
                    newDaily.setDate(k);
                    newDaily.setUsedCount(0);
                    newDaily.setUsedAmount(0);
                    return newDaily;
                });
                dailyUsage.setUsedCount(dailyUsage.getUsedCount() + 1);
                dailyUsage.setUsedAmount(dailyUsage.getUsedAmount() + perCouponAmount);
            }
        }

        // 计算使用率
        double usageRate = 0;
        if (issuedCount > 0) {
            usageRate = (double) usedCount / issuedCount;
        }

        detailVO.setIssuedCount(issuedCount);
        detailVO.setUsedCount(usedCount);
        detailVO.setExpiredCount(expiredCount);
        detailVO.setUsedAmount(usedAmount);
        detailVO.setUsageRate(usageRate);
        detailVO.setDailyUsageList(new ArrayList<>(dailyUsageMap.values()));

        return detailVO;
    }
}
