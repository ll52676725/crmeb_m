package com.zbkj.search.controller;

import com.zbkj.common.response.CommonResult;
import com.zbkj.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能搜索控制器
 * @author CRMEB
 * @since 2024-05-20
 */
@RestController
@RequestMapping("/api/search")
@Api(tags = "智能搜索")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 智能搜索
     * @param userId 用户ID
     * @param query 搜索查询
     * @return 搜索结果
     */
    @PostMapping("/smart")
    @ApiOperation(value = "智能搜索", notes = "支持自然语言查询，如'我昨天的订单发货了么？'")
    public CommonResult<Object> smartSearch(
            @ApiParam(value = "用户ID", required = true) @RequestParam Integer userId,
            @ApiParam(value = "搜索查询", required = true) @RequestParam String query) {
        return searchService.handleSearchRequest(userId, query);
    }

    /**
     * 意图识别测试
     * @param query 搜索查询
     * @return 意图识别结果
     */
    @PostMapping("/intent/test")
    @ApiOperation(value = "意图识别测试", notes = "测试自然语言意图识别功能")
    public CommonResult<Object> testIntentRecognition(
            @ApiParam(value = "搜索查询", required = true) @RequestParam String query) {
        return searchService.handleSearchRequest(null, query);
    }
}
