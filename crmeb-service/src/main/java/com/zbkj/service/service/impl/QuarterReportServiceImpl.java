package com.zbkj.service.service.impl;

import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.QuarterReportRequest;
import com.zbkj.common.response.QuarterReportResponse;
import com.zbkj.common.response.QuarterStatisticsResponse;
import com.zbkj.common.utils.ChartUtil;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.PdfExportUtil;
import com.zbkj.common.vo.OrderExcelVo;
import com.zbkj.service.service.QuarterReportService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.UserVisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuarterReportServiceImpl implements QuarterReportService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    @Override
    public String exportQuarterReport(QuarterReportRequest request) {
        try {
            int year = request.getYear();
            int quarter = request.getQuarter();

            String[] dateRange = getQuarterDateRange(year, quarter);
            String startDate = dateRange[0];
            String endDate = dateRange[1];

            String[] lastQuarterRange = getLastQuarterDateRange(year, quarter);
            String lastQuarterStart = lastQuarterRange[0];
            String lastQuarterEnd = lastQuarterRange[1];

            String[] lastYearSameQuarterRange = getQuarterDateRange(year - 1, quarter);
            String lastYearStart = lastYearSameQuarterRange[0];
            String lastYearEnd = lastYearSameQuarterRange[1];

            QuarterStatisticsResponse statistics = calculateStatistics(
                    startDate, endDate,
                    lastQuarterStart, lastQuarterEnd,
                    lastYearStart, lastYearEnd
            );

            Map<String, Number> monthlySalesTrend = getMonthlySalesTrend(year, quarter);
            Map<String, Number> lastYearSalesTrend = getMonthlySalesTrend(year - 1, quarter);

            Map<String, Number> monthlyOrderTrend = getMonthlyOrderTrend(year, quarter);
            Map<String, Number> lastYearOrderTrend = getMonthlyOrderTrend(year - 1, quarter);

            List<OrderExcelVo> orderList = getOrderDetailList(startDate, endDate);
            List<User> userList = getUserDetailList(startDate, endDate);

            String salesChartPath = ChartUtil.createLineChart(
                    "销售额趋势对比",
                    "月份",
                    "销售额（元）",
                    monthlySalesTrend,
                    lastYearSalesTrend,
                    year + "年",
                    (year - 1) + "年",
                    800,
                    400
            );

            String orderChartPath = ChartUtil.createLineChart(
                    "订单量趋势对比",
                    "月份",
                    "订单量",
                    monthlyOrderTrend,
                    lastYearOrderTrend,
                    year + "年",
                    (year - 1) + "年",
                    800,
                    400
            );

            String title = year + "年第" + quarter + "季度经营报表";
            String quarterInfo = getQuarterName(quarter) + "（" + startDate + " 至 " + endDate + "）";
            String generateTime = DateUtil.nowDateTimeStr();

            String pdfPath = PdfExportUtil.exportQuarterReport(
                    title,
                    quarterInfo,
                    statistics,
                    salesChartPath,
                    orderChartPath,
                    orderList,
                    userList,
                    generateTime
            );

            ChartUtil.deleteChartFile(salesChartPath);
            ChartUtil.deleteChartFile(orderChartPath);

            return pdfPath;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("季度报表导出失败: " + e.getMessage());
        }
    }

    private String[] getQuarterDateRange(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = quarter * 3;

        String startDate = year + "-" + String.format("%02d", startMonth) + "-01";
        
        Calendar cal = Calendar.getInstance();
        cal.set(year, endMonth - 1, 1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String endDate = year + "-" + String.format("%02d", endMonth) + "-" + lastDay;

        return new String[]{startDate, endDate};
    }

    private String[] getLastQuarterDateRange(int year, int quarter) {
        int lastQuarter = quarter - 1;
        int lastYear = year;
        if (lastQuarter < 1) {
            lastQuarter = 4;
            lastYear = year - 1;
        }
        return getQuarterDateRange(lastYear, lastQuarter);
    }

    private String getQuarterName(int quarter) {
        String[] names = {"第一季度", "第二季度", "第三季度", "第四季度"};
        return names[quarter - 1];
    }

    private QuarterStatisticsResponse calculateStatistics(
            String startDate, String endDate,
            String lastQuarterStart, String lastQuarterEnd,
            String lastYearStart, String lastYearEnd) {

        QuarterStatisticsResponse statistics = new QuarterStatisticsResponse();

        BigDecimal sales = storeOrderService.getPayOrderAmountByPeriod(startDate, endDate);
        BigDecimal lastQuarterSales = storeOrderService.getPayOrderAmountByPeriod(lastQuarterStart, lastQuarterEnd);
        BigDecimal lastYearSameQuarterSales = storeOrderService.getPayOrderAmountByPeriod(lastYearStart, lastYearEnd);

        statistics.setSales(sales);
        statistics.setLastQuarterSales(lastQuarterSales);
        statistics.setLastYearSameQuarterSales(lastYearSameQuarterSales);

        statistics.setSalesQuarterRate(calculateGrowthRate(sales, lastQuarterSales));
        statistics.setSalesYearRate(calculateGrowthRate(sales, lastYearSameQuarterSales));

        Integer orderNum = getOrderCountByPeriod(startDate, endDate);
        Integer lastQuarterOrderNum = getOrderCountByPeriod(lastQuarterStart, lastQuarterEnd);
        Integer lastYearSameQuarterOrderNum = getOrderCountByPeriod(lastYearStart, lastYearEnd);

        statistics.setOrderNum(orderNum);
        statistics.setLastQuarterOrderNum(lastQuarterOrderNum);
        statistics.setLastYearSameQuarterOrderNum(lastYearSameQuarterOrderNum);

        statistics.setOrderNumQuarterRate(calculateGrowthRate(new BigDecimal(orderNum), new BigDecimal(lastQuarterOrderNum)));
        statistics.setOrderNumYearRate(calculateGrowthRate(new BigDecimal(orderNum), new BigDecimal(lastYearSameQuarterOrderNum)));

        Integer newUserNum = userService.getRegisterNumByPeriod(startDate, endDate);
        Integer lastQuarterNewUserNum = userService.getRegisterNumByPeriod(lastQuarterStart, lastQuarterEnd);
        Integer lastYearSameQuarterNewUserNum = userService.getRegisterNumByPeriod(lastYearStart, lastYearEnd);

        statistics.setNewUserNum(newUserNum);
        statistics.setLastQuarterNewUserNum(lastQuarterNewUserNum);
        statistics.setLastYearSameQuarterNewUserNum(lastYearSameQuarterNewUserNum);

        statistics.setNewUserNumQuarterRate(calculateGrowthRate(new BigDecimal(newUserNum), new BigDecimal(lastQuarterNewUserNum)));
        statistics.setNewUserNumYearRate(calculateGrowthRate(new BigDecimal(newUserNum), new BigDecimal(lastYearSameQuarterNewUserNum)));

        BigDecimal avgOrderPrice = orderNum > 0 ? sales.divide(new BigDecimal(orderNum), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal lastQuarterAvgOrderPrice = lastQuarterOrderNum > 0 
                ? lastQuarterSales.divide(new BigDecimal(lastQuarterOrderNum), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;

        statistics.setAvgOrderPrice(avgOrderPrice);
        statistics.setLastQuarterAvgOrderPrice(lastQuarterAvgOrderPrice);
        statistics.setAvgOrderPriceRate(calculateGrowthRate(avgOrderPrice, lastQuarterAvgOrderPrice));

        Long pageviews = getPageviewsByPeriod(startDate, endDate);
        Long lastQuarterPageviews = getPageviewsByPeriod(lastQuarterStart, lastQuarterEnd);

        statistics.setPageviews(pageviews);
        statistics.setLastQuarterPageviews(lastQuarterPageviews);
        statistics.setPageviewsRate(calculateGrowthRate(new BigDecimal(pageviews), new BigDecimal(lastQuarterPageviews)));

        return statistics;
    }

    private BigDecimal calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Integer getOrderCountByPeriod(String startDate, String endDate) {
        int count = 0;
        Date start = DateUtil.strToDate(startDate, Constants.DATE_FORMAT_DATE);
        Date end = DateUtil.strToDate(endDate, Constants.DATE_FORMAT_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        while (!cal.getTime().after(end)) {
            String dateStr = DateUtil.dateToStr(cal.getTime(), Constants.DATE_FORMAT_DATE);
            Integer dayCount = storeOrderService.getPayOrderNumByDate(dateStr);
            count += dayCount != null ? dayCount : 0;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return count;
    }

    private Long getPageviewsByPeriod(String startDate, String endDate) {
        long count = 0;
        Date start = DateUtil.strToDate(startDate, Constants.DATE_FORMAT_DATE);
        Date end = DateUtil.strToDate(endDate, Constants.DATE_FORMAT_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        while (!cal.getTime().after(end)) {
            String dateStr = DateUtil.dateToStr(cal.getTime(), Constants.DATE_FORMAT_DATE);
            Long dayCount = userVisitRecordService.getPageviewsByDate(dateStr);
            count += dayCount != null ? dayCount : 0;
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return count;
    }

    private Map<String, Number> getMonthlySalesTrend(int year, int quarter) {
        Map<String, Number> trend = new LinkedHashMap<>();
        int startMonth = (quarter - 1) * 3 + 1;
        
        for (int i = 0; i < 3; i++) {
            int month = startMonth + i;
            String monthStr = month + "月";
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            String startDate = year + "-" + String.format("%02d", month) + "-01";
            String endDate = year + "-" + String.format("%02d", month) + "-" + lastDay;
            
            BigDecimal sales = storeOrderService.getPayOrderAmountByPeriod(startDate, endDate);
            trend.put(monthStr, sales != null ? sales.doubleValue() : 0);
        }
        return trend;
    }

    private Map<String, Number> getMonthlyOrderTrend(int year, int quarter) {
        Map<String, Number> trend = new LinkedHashMap<>();
        int startMonth = (quarter - 1) * 3 + 1;
        
        for (int i = 0; i < 3; i++) {
            int month = startMonth + i;
            String monthStr = month + "月";
            
            Calendar cal = Calendar.getInstance();
            cal.set(year, month - 1, 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            
            String startDate = year + "-" + String.format("%02d", month) + "-01";
            String endDate = year + "-" + String.format("%02d", month) + "-" + lastDay;
            
            Integer orderCount = getOrderCountByPeriod(startDate, endDate);
            trend.put(monthStr, orderCount != null ? orderCount : 0);
        }
        return trend;
    }

    private List<OrderExcelVo> getOrderDetailList(String startDate, String endDate) {
        List<StoreOrder> orders = storeOrderService.lambdaQuery()
                .between(StoreOrder::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .eq(StoreOrder::getPaid, 1)
                .orderByDesc(StoreOrder::getCreateTime)
                .last("LIMIT 100")
                .list();

        return orders.stream().map(order -> {
            OrderExcelVo vo = new OrderExcelVo();
            vo.setOrderId(order.getOrderId());
            vo.setPayPrice(order.getPayPrice().toString());
            vo.setCreateTime(DateUtil.dateToStr(order.getCreateTime(), Constants.DATE_FORMAT));
            vo.setStatusStr(getOrderStatusStr(order.getStatus()));
            vo.setRealName(order.getRealName());
            return vo;
        }).collect(Collectors.toList());
    }

    private List<User> getUserDetailList(String startDate, String endDate) {
        return userService.lambdaQuery()
                .between(User::getCreateTime, startDate + " 00:00:00", endDate + " 23:59:59")
                .orderByDesc(User::getCreateTime)
                .last("LIMIT 100")
                .list();
    }

    private String getOrderStatusStr(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待发货";
            case 1: return "待收货";
            case 2: return "待评价";
            case 3: return "已完成";
            default: return "未知";
        }
    }
}
