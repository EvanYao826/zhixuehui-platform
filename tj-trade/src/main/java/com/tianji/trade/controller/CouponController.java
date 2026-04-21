package com.tianji.trade.controller;

import com.tianji.common.annotation.Anonymous;
import com.tianji.trade.domain.vo.CouponStatsVO;
import com.tianji.trade.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 优惠券控制器
 */
@RestController
@RequestMapping("/trade/coupons")
@Api(tags = "优惠券相关接口")
@Anonymous
public class CouponController {

    @Autowired
    private ICouponService couponService;

    @GetMapping("/stats")
    @ApiOperation("获取优惠券统计数据")
    public CouponStatsVO getCouponStats(
            @ApiParam(value = "开始日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @ApiParam(value = "结束日期，格式：yyyy-MM-dd", example = "2024-01-31")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate) {
        return couponService.getCouponStats(startDate, endDate);
    }

    @GetMapping("/usage")
    @ApiOperation("获取优惠券使用情况")
    public CouponStatsVO.CouponUsageDetailVO getCouponUsageDetail(
            @ApiParam(value = "优惠券ID")
            @RequestParam(required = false) Long couponId,
            @ApiParam(value = "开始日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate,
            @ApiParam(value = "结束日期，格式：yyyy-MM-dd", example = "2024-01-31")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate endDate) {
        return couponService.getCouponUsageDetail(couponId, startDate, endDate);
    }
}
