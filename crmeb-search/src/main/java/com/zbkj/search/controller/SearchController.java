package com.zbkj.search.controller;

import com.zbkj.common.response.SearchResponse;
import com.zbkj.search.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 智能搜索控制器
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/search")
@Api(tags = "智能搜索")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 智能搜索
     * @param keyword 搜索关键词
     * @param uid 用户ID
     * @return SearchResponse 搜索结果
     */
    @GetMapping("/intelligent")
    @ApiOperation(value = "智能搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键词", required = true, dataType = "String"),
            @ApiImplicitParam(name = "uid", value = "用户ID", required = true, dataType = "Integer")
    })
    public SearchResponse intelligentSearch(@RequestParam String keyword, @RequestParam Integer uid) {
        return searchService.intelligentSearch(keyword, uid);
    }
}