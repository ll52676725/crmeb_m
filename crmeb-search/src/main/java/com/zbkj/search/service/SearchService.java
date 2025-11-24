package com.zbkj.search.service;

import com.zbkj.search.request.SearchRequest;
import com.zbkj.search.response.SearchResponse;

/**
 * 搜索服务接口
 */
public interface SearchService {

    /**
     * 智能搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchResponse intelligentSearch(SearchRequest request);

    /**
     * 订单搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchResponse searchOrders(SearchRequest request);

    /**
     * 商品搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchResponse searchProducts(SearchRequest request);

    /**
     * 文章搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    SearchResponse searchArticles(SearchRequest request);

}