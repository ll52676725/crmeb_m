package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="QuarterStatisticsResponse对象", description="季度统计数据")
public class QuarterStatisticsResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "本季度销售额")
    private BigDecimal sales;

    @ApiModelProperty(value = "上季度销售额")
    private BigDecimal lastQuarterSales;

    @ApiModelProperty(value = "去年同季度销售额")
    private BigDecimal lastYearSameQuarterSales;

    @ApiModelProperty(value = "销售额环比增长率")
    private BigDecimal salesQuarterRate;

    @ApiModelProperty(value = "销售额同比增长率")
    private BigDecimal salesYearRate;

    @ApiModelProperty(value = "本季度订单量")
    private Integer orderNum;

    @ApiModelProperty(value = "上季度订单量")
    private Integer lastQuarterOrderNum;

    @ApiModelProperty(value = "去年同季度订单量")
    private Integer lastYearSameQuarterOrderNum;

    @ApiModelProperty(value = "订单量环比增长率")
    private BigDecimal orderNumQuarterRate;

    @ApiModelProperty(value = "订单量同比增长率")
    private BigDecimal orderNumYearRate;

    @ApiModelProperty(value = "本季度新增用户")
    private Integer newUserNum;

    @ApiModelProperty(value = "上季度新增用户")
    private Integer lastQuarterNewUserNum;

    @ApiModelProperty(value = "去年同季度新增用户")
    private Integer lastYearSameQuarterNewUserNum;

    @ApiModelProperty(value = "新增用户环比增长率")
    private BigDecimal newUserNumQuarterRate;

    @ApiModelProperty(value = "新增用户同比增长率")
    private BigDecimal newUserNumYearRate;

    @ApiModelProperty(value = "本季度客单价")
    private BigDecimal avgOrderPrice;

    @ApiModelProperty(value = "上季度客单价")
    private BigDecimal lastQuarterAvgOrderPrice;

    @ApiModelProperty(value = "客单价环比增长率")
    private BigDecimal avgOrderPriceRate;

    @ApiModelProperty(value = "本季度访问量")
    private Long pageviews;

    @ApiModelProperty(value = "上季度访问量")
    private Long lastQuarterPageviews;

    @ApiModelProperty(value = "访问量环比增长率")
    private BigDecimal pageviewsRate;
}
