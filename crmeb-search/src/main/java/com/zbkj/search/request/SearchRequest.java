package com.zbkj.search.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 搜索请求实体类
 */
@Data
@ApiModel(value = "搜索请求实体类", description = "搜索请求实体类")
public class SearchRequest {

    @NotBlank(message = "搜索关键词不能为空")
    @ApiModelProperty(value = "搜索关键词", required = true)
    private String keyword;

    @ApiModelProperty(value = "用户ID", required = true)
    private Integer uid;

    @ApiModelProperty(value = "搜索类型", example = "all")
    private String type = "all";

}