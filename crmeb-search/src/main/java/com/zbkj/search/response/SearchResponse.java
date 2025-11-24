package com.zbkj.search.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 搜索响应实体类
 */
@Data
@ApiModel(value = "搜索响应实体类", description = "搜索响应实体类")
public class SearchResponse {

    @ApiModelProperty(value = "搜索关键词")
    private String keyword;

    @ApiModelProperty(value = "搜索类型")
    private String type;

    @ApiModelProperty(value = "订单搜索结果")
    private List<OrderSearchResult> orderResults;

    @ApiModelProperty(value = "商品搜索结果")
    private List<ProductSearchResult> productResults;

    @ApiModelProperty(value = "文章搜索结果")
    private List<ArticleSearchResult> articleResults;

    @ApiModelProperty(value = "搜索建议")
    private List<String> suggestions;

    /**
     * 订单搜索结果实体类
     */
    @Data
    @ApiModel(value = "订单搜索结果实体类", description = "订单搜索结果实体类")
    public static class OrderSearchResult {

        @ApiModelProperty(value = "订单ID")
        private Integer id;

        @ApiModelProperty(value = "订单号")
        private String orderId;

        @ApiModelProperty(value = "订单状态")
        private Integer status;

        @ApiModelProperty(value = "订单状态描述")
        private String statusStr;

        @ApiModelProperty(value = "支付金额")
        private String payPrice;

        @ApiModelProperty(value = "创建时间")
        private String createTime;

        @ApiModelProperty(value = "商品名称")
        private String productName;
    }

    /**
     * 商品搜索结果实体类
     */
    @Data
    @ApiModel(value = "商品搜索结果实体类", description = "商品搜索结果实体类")
    public static class ProductSearchResult {

        @ApiModelProperty(value = "商品ID")
        private Integer id;

        @ApiModelProperty(value = "商品名称")
        private String name;

        @ApiModelProperty(value = "商品价格")
        private String price;

        @ApiModelProperty(value = "商品图片")
        private String image;

        @ApiModelProperty(value = "商品销量")
        private Integer sales;
    }

    /**
     * 文章搜索结果实体类
     */
    @Data
    @ApiModel(value = "文章搜索结果实体类", description = "文章搜索结果实体类")
    public static class ArticleSearchResult {

        @ApiModelProperty(value = "文章ID")
        private Integer id;

        @ApiModelProperty(value = "文章标题")
        private String title;

        @ApiModelProperty(value = "文章简介")
        private String summary;

        @ApiModelProperty(value = "文章图片")
        private String image;

        @ApiModelProperty(value = "发布时间")
        private String createTime;
    }
}