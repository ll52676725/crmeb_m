package com.zbkj.common.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class ChartUtil {

    private static final String CHART_PATH = UploadUtil.getServerPath() + "charts/";

    static {
        StandardChartTheme theme = new StandardChartTheme("CN");
        theme.setExtraLargeFont(new Font("宋体", Font.BOLD, 20));
        theme.setLargeFont(new Font("宋体", Font.BOLD, 16));
        theme.setRegularFont(new Font("宋体", Font.PLAIN, 12));
        ChartFactory.setChartTheme(theme);
    }

    public static String createLineChart(String title, String xAxisLabel, String yAxisLabel,
                                         Map<String, Number> currentData, Map<String, Number> lastData,
                                         String currentSeriesName, String lastSeriesName,
                                         int width, int height) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Map.Entry<String, Number> entry : currentData.entrySet()) {
            dataset.addValue(entry.getValue(), currentSeriesName, entry.getKey());
        }
        
        if (lastData != null) {
            for (Map.Entry<String, Number> entry : lastData.entrySet()) {
                dataset.addValue(entry.getValue(), lastSeriesName, entry.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 248, 255));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", new DecimalFormat("#.##")));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));

        File chartDir = new File(CHART_PATH);
        if (!chartDir.exists()) {
            chartDir.mkdirs();
        }

        String fileName = "line_chart_" + System.currentTimeMillis() + ".png";
        File chartFile = new File(CHART_PATH + fileName);
        ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

        return CHART_PATH + fileName;
    }

    public static String createBarChart(String title, String xAxisLabel, String yAxisLabel,
                                        Map<String, Number> currentData, Map<String, Number> lastData,
                                        String currentSeriesName, String lastSeriesName,
                                        int width, int height) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (Map.Entry<String, Number> entry : currentData.entrySet()) {
            dataset.addValue(entry.getValue(), currentSeriesName, entry.getKey());
        }
        
        if (lastData != null) {
            for (Map.Entry<String, Number> entry : lastData.entrySet()) {
                dataset.addValue(entry.getValue(), lastSeriesName, entry.getKey());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 248, 255));
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", new DecimalFormat("#.##")));
        renderer.setItemMargin(0.1);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
        domainAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new Font("宋体", Font.PLAIN, 12));
        rangeAxis.setLabelFont(new Font("宋体", Font.BOLD, 14));

        File chartDir = new File(CHART_PATH);
        if (!chartDir.exists()) {
            chartDir.mkdirs();
        }

        String fileName = "bar_chart_" + System.currentTimeMillis() + ".png";
        File chartFile = new File(CHART_PATH + fileName);
        ChartUtils.saveChartAsPNG(chartFile, chart, width, height);

        return CHART_PATH + fileName;
    }

    public static void deleteChartFile(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
