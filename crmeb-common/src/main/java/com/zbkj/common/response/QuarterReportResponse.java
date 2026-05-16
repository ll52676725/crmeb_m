package com.zbkj.common.response;

import com.zbkj.common.model.user.User;
import com.zbkj.common.vo.OrderExcelVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="QuarterReportResponse对象", description="季度报表完整数据")
public class QuarterReportResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "报表标题")
    private String title;

    @ApiModelProperty(value = "季度信息")
    private String quarterInfo;

    @ApiModelProperty(value = "统计数据")
    private QuarterStatisticsResponse statistics;

    @ApiModelProperty(value = "月度销售趋势数据")
    private Map<String, Object> monthlySalesTrend;

    @ApiModelProperty(value = "月度订单趋势数据")
    private Map<String, Object> monthlyOrderTrend;

    @ApiModelProperty(value = "订单明细列表（前100条）")
    private List<OrderExcelVo> orderDetailList;

    @ApiModelProperty(value = "新增用户明细列表（前100条）")
    private List<User> userDetailList;

    @ApiModelProperty(value = "生成时间")
    private String generateTime;
}
