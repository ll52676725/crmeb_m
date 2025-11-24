package com.zbkj.search.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.StoreOrderSearchRequest;
import com.zbkj.common.request.StoreProductSearchRequest;
import com.zbkj.common.response.StoreOrderDetailResponse;
import com.zbkj.common.response.StoreProductResponse;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.StoreProductService;
import com.zbkj.search.request.SearchRequest;
import com.zbkj.search.response.SearchResponse;
import com.zbkj.search.service.SearchService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreProductService storeProductService;

    /**
     * 智能搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @Override
    public SearchResponse intelligentSearch(SearchRequest request) {
        String keyword = request.getKeyword();
        Integer uid = request.getUid();

        // 使用HanLP进行中文分词
        List<Term> terms = HanLP.segment(keyword);

        // 分析搜索意图
        SearchIntent intent = analyzeSearchIntent(terms, uid);

        // 根据搜索意图执行相应的搜索
        switch (intent.getType()) {
            case ORDER:
                return searchOrders(request);
            case PRODUCT:
                return searchProducts(request);
            case ARTICLE:
                return searchArticles(request);
            default:
                // 默认搜索所有类型
                SearchResponse response = new SearchResponse();
                response.setKeyword(keyword);
                response.setType("all");

                // 搜索订单
                SearchResponse orderResponse = searchOrders(request);
                response.setOrderResults(orderResponse.getOrderResults());

                // 搜索商品
                SearchResponse productResponse = searchProducts(request);
                response.setProductResults(productResponse.getProductResults());

                // 搜索文章
                SearchResponse articleResponse = searchArticles(request);
                response.setArticleResults(articleResponse.getArticleResults());

                return response;
        }
    }

    /**
     * 订单搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @Override
    public SearchResponse searchOrders(SearchRequest request) {
        String keyword = request.getKeyword();
        Integer uid = request.getUid();

        SearchResponse response = new SearchResponse();
        response.setKeyword(keyword);
        response.setType("order");

        // 创建订单搜索请求
        StoreOrderSearchRequest orderSearchRequest = new StoreOrderSearchRequest();
        orderSearchRequest.setOrderNo(keyword);
        orderSearchRequest.setType(0); // 普通订单

        // 获取用户订单列表
        CommonPage<StoreOrderDetailResponse> orderPage = storeOrderService.getAdminList(orderSearchRequest, null);
        List<StoreOrderDetailResponse> orderDetailList = orderPage.getList();

        // 转换为订单搜索结果
        List<SearchResponse.OrderSearchResult> orderResults = new ArrayList<>();
        if (orderDetailList != null && !orderDetailList.isEmpty()) {
            orderResults = orderDetailList.stream().map(orderDetail -> {
                SearchResponse.OrderSearchResult result = new SearchResponse.OrderSearchResult();
                result.setId(orderDetail.getId());
                result.setOrderId(orderDetail.getOrderId());
                result.setStatus(orderDetail.getStatus());
                result.setStatusStr(getOrderStatusStr(orderDetail.getStatus()));
                result.setPayPrice(orderDetail.getPayPrice().toString());
                result.setCreateTime(DateUtil.format(orderDetail.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
                // 这里可以添加商品名称，需要从订单详情中获取
                result.setProductName("商品名称");
                return result;
            }).collect(Collectors.toList());
        }

        response.setOrderResults(orderResults);
        return response;
    }

    /**
     * 商品搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @Override
    public SearchResponse searchProducts(SearchRequest request) {
        String keyword = request.getKeyword();

        SearchResponse response = new SearchResponse();
        response.setKeyword(keyword);
        response.setType("product");

        // 搜索商品
        StoreProductSearchRequest productSearchRequest = new StoreProductSearchRequest();
        productSearchRequest.setType(1); // 出售中
        productSearchRequest.setKeywords(keyword);
        PageInfo<StoreProductResponse> productPage = storeProductService.getAdminList(productSearchRequest, null);
        List<StoreProductResponse> productResponseList = productPage.getList();

        // 转换为商品搜索结果
        List<SearchResponse.ProductSearchResult> productResults = new ArrayList<>();
        if (productResponseList != null && !productResponseList.isEmpty()) {
            productResults = productResponseList.stream().map(productResponse -> {
                SearchResponse.ProductSearchResult result = new SearchResponse.ProductSearchResult();
                result.setId(productResponse.getId());
                result.setName(productResponse.getName());
                result.setPrice(productResponse.getPrice().toString());
                result.setImage(productResponse.getImage());
                result.setSales(productResponse.getSales());
                return result;
            }).collect(Collectors.toList());
        }

        response.setProductResults(productResults);
        return response;
    }

    /**
     * 文章搜索
     * @param request 搜索请求
     * @return 搜索响应
     */
    @Override
    public SearchResponse searchArticles(SearchRequest request) {
        String keyword = request.getKeyword();

        SearchResponse response = new SearchResponse();
        response.setKeyword(keyword);
        response.setType("article");

        // 文章搜索功能暂未实现
        response.setArticleResults(new ArrayList<>());
        return response;
    }

    /**
     * 分析搜索意图
     * @param terms 分词结果
     * @param uid 用户ID
     * @return 搜索意图
     */
    private SearchIntent analyzeSearchIntent(List<Term> terms, Integer uid) {
        SearchIntent intent = new SearchIntent();
        intent.setType(SearchIntentType.ALL);

        // 关键词列表
        List<String> keywords = terms.stream().map(Term::toString).collect(Collectors.toList());

        // 检查是否包含订单相关关键词
        List<String> orderKeywords = new ArrayList<>();
        orderKeywords.add("订单");
        orderKeywords.add("发货");
        orderKeywords.add("物流");
        orderKeywords.add("退款");
        orderKeywords.add("退货");
        orderKeywords.add("支付");
        orderKeywords.add("购买");
        boolean hasOrderKeyword = keywords.stream().anyMatch(orderKeywords::contains);

        // 检查是否包含商品相关关键词
        List<String> productKeywords = new ArrayList<>();
        productKeywords.add("商品");
        productKeywords.add("产品");
        productKeywords.add("购买");
        productKeywords.add("价格");
        productKeywords.add("优惠");
        productKeywords.add("折扣");
        boolean hasProductKeyword = keywords.stream().anyMatch(productKeywords::contains);

        // 检查是否包含文章相关关键词
        List<String> articleKeywords = new ArrayList<>();
        articleKeywords.add("文章");
        articleKeywords.add("资讯");
        articleKeywords.add("新闻");
        articleKeywords.add("攻略");
        articleKeywords.add("教程");
        boolean hasArticleKeyword = keywords.stream().anyMatch(articleKeywords::contains);

        // 确定搜索意图
        if (hasOrderKeyword) {
            intent.setType(SearchIntentType.ORDER);
        } else if (hasProductKeyword) {
            intent.setType(SearchIntentType.PRODUCT);
        } else if (hasArticleKeyword) {
            intent.setType(SearchIntentType.ARTICLE);
        }

        // 检查是否包含时间关键词
        String timeKeyword = checkTimeKeyword(keywords);
        if (StrUtil.isNotBlank(timeKeyword)) {
            intent.setTimeKeyword(timeKeyword);
        }

        return intent;
    }

    /**
     * 检查时间关键词
     * @param keywords 关键词列表
     * @return 时间关键词
     */
    private String checkTimeKeyword(List<String> keywords) {
        List<String> timeKeywords = new ArrayList<>();
        timeKeywords.add("今天");
        timeKeywords.add("昨天");
        timeKeywords.add("前天");
        timeKeywords.add("本周");
        timeKeywords.add("上周");
        timeKeywords.add("本月");
        timeKeywords.add("上月");
        timeKeywords.add("今年");
        timeKeywords.add("去年");
        for (String keyword : keywords) {
            if (timeKeywords.contains(keyword)) {
                return keyword;
            }
        }
        return null;
    }

    /**
     * 获取订单状态描述
     * @param status 订单状态
     * @return 订单状态描述
     */
    private String getOrderStatusStr(Integer status) {
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "待发货";
            case 2:
                return "待收货";
            case 3:
                return "待评价";
            case 4:
                return "已完成";
            case -3:
                return "售后/退款";
            default:
                return "未知状态";
        }
    }

    /**
     * 搜索意图类
     */
    private static class SearchIntent {
        private SearchIntentType type;
        private String timeKeyword;

        public SearchIntentType getType() {
            return type;
        }

        public void setType(SearchIntentType type) {
            this.type = type;
        }

        public String getTimeKeyword() {
            return timeKeyword;
        }

        public void setTimeKeyword(String timeKeyword) {
            this.timeKeyword = timeKeyword;
        }
    }

    /**
     * 搜索意图类型枚举
     */
    private enum SearchIntentType {
        ALL, ORDER, PRODUCT, ARTICLE
    }
}