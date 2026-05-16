package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="QuarterReportRequest对象", description="季度报表请求参数")
public class QuarterReportRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "年份", required = true, example = "2024")
    @NotNull(message = "年份不能为空")
    @Min(value = 2020, message = "年份不能小于2020")
    private Integer year;

    @ApiModelProperty(value = "季度(1-4)", required = true, example = "1")
    @NotNull(message = "季度不能为空")
    private Integer quarter;
}
