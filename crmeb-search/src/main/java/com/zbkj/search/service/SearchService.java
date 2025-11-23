package com.zbkj.search.service;

import com.zbkj.common.response.CommonResult;
import com.zbkj.search.model.IntentRecognitionResult;

/**
 * 搜索服务接口
 * @author CRMEB
 * @since 2024-05-20
 */
public interface SearchService {

    /**
     * 处理用户搜索请求
     * @param userId 用户ID
     * @param query 搜索查询
     * @return 搜索结果
     */
    CommonResult<Object> handleSearchRequest(Integer userId, String query);

    /**
     * 根据意图执行搜索
     * @param userId 用户ID
     * @param intentResult 意图识别结果
     * @return 搜索结果
     */
    CommonResult<Object> executeSearchByIntent(Integer userId, IntentRecognitionResult intentResult);
}
