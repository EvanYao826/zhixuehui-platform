package com.tianji.data.controller;

import com.tianji.common.annotation.Anonymous;
import com.tianji.data.model.vo.DailyReportVO;
import com.tianji.data.model.vo.WeeklyReportVO;
import com.tianji.data.service.ReportService;
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
 * 报表控制器
 */
@RestController
@RequestMapping("/data/report")
@Api(tags = "报表相关接口")
@Anonymous
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/daily")
    @ApiOperation("获取日报")
    public DailyReportVO getDailyReport(
            @ApiParam(value = "日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate date) {
        return reportService.getDailyReport(date);
    }

    @GetMapping("/weekly")
    @ApiOperation("获取周报")
    public WeeklyReportVO getWeeklyReport(
            @ApiParam(value = "周的开始日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate) {
        return reportService.getWeeklyReport(startDate);
    }

    @GetMapping("/generate/daily")
    @ApiOperation("一键生成日报")
    public void generateDailyReport(
            @ApiParam(value = "日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate date) {
        reportService.generateDailyReport(date);
    }

    @GetMapping("/generate/weekly")
    @ApiOperation("一键生成周报")
    public void generateWeeklyReport(
            @ApiParam(value = "周的开始日期，格式：yyyy-MM-dd", example = "2024-01-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate startDate) {
        reportService.generateWeeklyReport(startDate);
    }
}
