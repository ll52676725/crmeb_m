package com.zbkj.search.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.search.request.SearchRequest;
import com.zbkj.search.response.SearchResponse;
import com.zbkj.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@Api(tags = "搜索相关接口")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 智能搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @PostMapping("/intelligent")
    @ApiOperation(value = "智能搜索", notes = "智能搜索，根据用户输入分析意图并返回相应结果")
    public CommonResult<SearchResponse> intelligentSearch(@Validated @RequestBody SearchRequest request) {
        SearchResponse response = searchService.intelligentSearch(request);
        return CommonResult.success(response);
    }

    /**
     * 订单搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @PostMapping("/orders")
    @ApiOperation(value = "订单搜索", notes = "搜索用户的订单信息")
    public CommonResult<SearchResponse> searchOrders(@Validated @RequestBody SearchRequest request) {
        SearchResponse response = searchService.searchOrders(request);
        return CommonResult.success(response);
    }

    /**
     * 商品搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @PostMapping("/products")
    @ApiOperation(value = "商品搜索", notes = "搜索商城的商品信息")
    public CommonResult<SearchResponse> searchProducts(@Validated @RequestBody SearchRequest request) {
        SearchResponse response = searchService.searchProducts(request);
        return CommonResult.success(response);
    }

    /**
     * 文章搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @PostMapping("/articles")
    @ApiOperation(value = "文章搜索", notes = "搜索商城的文章资讯")
    public CommonResult<SearchResponse> searchArticles(@Validated @RequestBody SearchRequest request) {
        SearchResponse response = searchService.searchArticles(request);
        return CommonResult.success(response);
    }
}