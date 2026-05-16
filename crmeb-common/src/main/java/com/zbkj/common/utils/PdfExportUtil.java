package com.zbkj.common.utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.response.QuarterStatisticsResponse;
import com.zbkj.common.vo.OrderExcelVo;
import com.zbkj.common.model.user.User;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class PdfExportUtil {

    private static final String PDF_PATH = UploadUtil.getServerPath() + "pdf/";
    
    private static Font titleFont;
    private static Font headerFont;
    private static Font contentFont;
    private static Font smallFont;

    static {
        try {
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titleFont = new Font(baseFont, 20, Font.BOLD);
            headerFont = new Font(baseFont, 14, Font.BOLD);
            contentFont = new Font(baseFont, 10, Font.NORMAL);
            smallFont = new Font(baseFont, 8, Font.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String exportQuarterReport(String title, String quarterInfo,
                                             QuarterStatisticsResponse statistics,
                                             String salesChartPath, String orderChartPath,
                                             List<OrderExcelVo> orderList, List<User> userList,
                                             String generateTime) throws Exception {
        File pdfDir = new File(PDF_PATH);
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }

        String fileName = "quarter_report_" + System.currentTimeMillis() + ".pdf";
        String filePath = PDF_PATH + fileName;

        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        addTitle(document, title);
        addReportInfo(document, quarterInfo, generateTime);
        addStatisticsSection(document, statistics);
        addChartSection(document, salesChartPath, "销售额趋势对比");
        addChartSection(document, orderChartPath, "订单量趋势对比");
        addOrderDetailSection(document, orderList);
        addUserDetailSection(document, userList);

        document.close();

        return UploadUtil.getWebPath() + "pdf/" + fileName;
    }

    private static void addTitle(Document document, String title) throws DocumentException {
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(20);
        document.add(titlePara);
    }

    private static void addReportInfo(Document document, String quarterInfo, String generateTime) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);

        PdfPCell cell1 = new PdfPCell(new Paragraph("报表周期: " + quarterInfo, contentFont));
        cell1.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Paragraph("生成时间: " + generateTime, contentFont));
        cell2.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell2);

        document.add(table);
    }

    private static void addStatisticsSection(Document document, QuarterStatisticsResponse statistics) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("一、核心统计数据", headerFont);
        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2});

        addTableHeader(table, "指标", "本季度", "环比", "同比");

        addTableRow(table, "销售额", 
            formatAmount(statistics.getSales()), 
            formatRate(statistics.getSalesQuarterRate()),
            formatRate(statistics.getSalesYearRate()));

        addTableRow(table, "订单量", 
            statistics.getOrderNum().toString(), 
            formatRate(statistics.getOrderNumQuarterRate()),
            formatRate(statistics.getOrderNumYearRate()));

        addTableRow(table, "新增用户", 
            statistics.getNewUserNum().toString(), 
            formatRate(statistics.getNewUserNumQuarterRate()),
            formatRate(statistics.getNewUserNumYearRate()));

        addTableRow(table, "客单价", 
            formatAmount(statistics.getAvgOrderPrice()), 
            formatRate(statistics.getAvgOrderPriceRate()),
            "-");

        addTableRow(table, "访问量", 
            statistics.getPageviews().toString(), 
            formatRate(statistics.getPageviewsRate()),
            "-");

        document.add(table);
    }

    private static void addChartSection(Document document, String chartPath, String title) throws DocumentException, IOException {
        Paragraph sectionTitle = new Paragraph(title, headerFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        if (chartPath != null && new File(chartPath).exists()) {
            com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(chartPath);
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin()) / img.getWidth()) * 100;
            img.scalePercent(scaler);
            img.setAlignment(Element.ALIGN_CENTER);
            document.add(img);
        }
    }

    private static void addOrderDetailSection(Document document, List<OrderExcelVo> orderList) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("三、订单明细（前50条）", headerFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        if (orderList == null || orderList.isEmpty()) {
            document.add(new Paragraph("暂无订单数据", contentFont));
            return;
        }

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2, 2});

        addTableHeader(table, "订单号", "用户", "金额", "状态", "创建时间");

        int count = Math.min(orderList.size(), 50);
        for (int i = 0; i < count; i++) {
            OrderExcelVo order = orderList.get(i);
            addTableRowSmall(table, 
                order.getOrderId(),
                order.getRealName(),
                order.getPayPrice(),
                order.getStatusStr(),
                order.getCreateTime());
        }

        document.add(table);
    }

    private static void addUserDetailSection(Document document, List<User> userList) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("四、新增用户明细（前50条）", headerFont);
        sectionTitle.setSpacingBefore(20);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        if (userList == null || userList.isEmpty()) {
            document.add(new Paragraph("暂无用户数据", contentFont));
            return;
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 2, 2, 2});

        addTableHeader(table, "用户ID", "昵称", "手机号", "注册时间");

        int count = Math.min(userList.size(), 50);
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            addTableRowSmall(table, 
                user.getUid().toString(),
                user.getNickname(),
                user.getPhone(),
                DateUtil.dateToStr(user.getCreateTime(), Constants.DATE_FORMAT));
        }

        document.add(table);
    }

    private static void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Paragraph(header, contentFont));
            cell.setBackgroundColor(new Color(240, 248, 255));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addTableRow(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Paragraph(value != null ? value : "-", contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private static void addTableRowSmall(PdfPTable table, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Paragraph(value != null ? value : "-", smallFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3);
            table.addCell(cell);
        }
    }

    private static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return "¥" + amount.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }

    private static String formatRate(BigDecimal rate) {
        if (rate == null) {
            return "-";
        }
        String symbol = rate.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return symbol + rate.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%";
    }
}
