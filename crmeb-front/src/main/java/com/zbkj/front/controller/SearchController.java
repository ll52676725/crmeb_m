package com.zbkj.front.controller;

import com.zbkj.common.response.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 智能搜索控制器
 * @author CRMEB
 * @since 2024-05-20
 */
@RestController
@RequestMapping("/api/front/search")
@Api(tags = "智能搜索")
public class SearchController {

    @Autowired
    private RestTemplate restTemplate;

    // crmeb-search模块的地址
    private static final String SEARCH_SERVICE_URL = "http://localhost:8085/search/api/search";

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
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            // 构建请求参数
            String requestBody = "userId=" + userId + "&query=" + java.net.URLEncoder.encode(query, "UTF-8");

            // 发送POST请求到crmeb-search模块
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<CommonResult> response = restTemplate.exchange(
                    SEARCH_SERVICE_URL + "/smart",
                    HttpMethod.POST,
                    entity,
                    CommonResult.class);

            // 返回搜索结果
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("智能搜索失败: " + e.getMessage());
        }
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
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            // 构建请求参数
            String requestBody = "query=" + java.net.URLEncoder.encode(query, "UTF-8");

            // 发送POST请求到crmeb-search模块
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<CommonResult> response = restTemplate.exchange(
                    SEARCH_SERVICE_URL + "/intent/test",
                    HttpMethod.POST,
                    entity,
                    CommonResult.class);

            // 返回意图识别结果
            return response.getBody();
        } catch (Exception e) {
            return CommonResult.failed("意图识别测试失败: " + e.getMessage());
        }
    }
}
