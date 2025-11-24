package com.zbkj.search.service.impl;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.article.Article;
import com.zbkj.common.response.SearchResponse;
import com.zbkj.search.service.SearchService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智能搜索服务实现类
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
@Service
public class SearchServiceImpl implements SearchService {

    /**
     * 智能搜索
     * @param keyword 搜索关键词
     * @param uid 用户ID
     * @return SearchResponse 搜索结果
     */
    @Override
    public SearchResponse intelligentSearch(String keyword, Integer uid) {
        SearchResponse response = new SearchResponse();
        List<Term> terms = HanLP.segment(keyword);
        List<String> keywords = terms.stream().map(Term::toString).collect(Collectors.toList());

        // 分析搜索意图
        String intent = analyzeSearchIntent(keywords);
        response.setIntent(intent);

        // 根据意图执行相应的搜索
        switch (intent) {
            case "order_status":
                response.setOrderList(searchOrdersByStatus(keywords, uid));
                break;
            case "product_search":
                response.setProductList(searchProducts(keywords));
                break;
            case "article_search":
                response.setArticleList(searchArticles(keywords));
                break;
            default:
                // 通用搜索，返回所有可能的结果
                response.setOrderList(searchOrdersByStatus(keywords, uid));
                response.setProductList(searchProducts(keywords));
                response.setArticleList(searchArticles(keywords));
        }

        // 生成搜索建议
        response.setSuggestions(generateSuggestions(keywords));

        return response;
    }

    /**
     * 分析搜索意图
     * @param keywords 关键词列表
     * @return String 搜索意图
     */
    private String analyzeSearchIntent(List<String> keywords) {
        // 订单状态相关关键词
        List<String> orderKeywords = new ArrayList<>();
        orderKeywords.add("订单");
        orderKeywords.add("发货");
        orderKeywords.add("物流");
        orderKeywords.add("状态");
        orderKeywords.add("购买");
        orderKeywords.add("昨天");
        orderKeywords.add("今天");
        orderKeywords.add("明天");
        orderKeywords.add("前天");

        // 商品相关关键词
        List<String> productKeywords = new ArrayList<>();
        productKeywords.add("商品");
        productKeywords.add("购买");
        productKeywords.add("产品");
        productKeywords.add("价格");
        productKeywords.add("优惠");
        productKeywords.add("折扣");

        // 文章相关关键词
        List<String> articleKeywords = new ArrayList<>();
        articleKeywords.add("文章");
        articleKeywords.add("资讯");
        articleKeywords.add("新闻");
        articleKeywords.add("攻略");
        articleKeywords.add("教程");

        // 统计关键词匹配次数
        int orderCount = 0;
        int productCount = 0;
        int articleCount = 0;

        for (String keyword : keywords) {
            if (orderKeywords.contains(keyword)) {
                orderCount++;
            }
            if (productKeywords.contains(keyword)) {
                productCount++;
            }
            if (articleKeywords.contains(keyword)) {
                articleCount++;
            }
        }

        // 确定主要意图
        if (orderCount >= productCount && orderCount >= articleCount) {
            return "order_status";
        } else if (productCount >= orderCount && productCount >= articleCount) {
            return "product_search";
        } else if (articleCount >= orderCount && articleCount >= productCount) {
            return "article_search";
        } else {
            return "general_search";
        }
    }

    /**
     * 根据状态搜索订单
     * @param keywords 关键词列表
     * @param uid 用户ID
     * @return List<StoreOrder> 订单列表
     */
    private List<StoreOrder> searchOrdersByStatus(List<String> keywords, Integer uid) {
        // 这里应该调用订单服务获取订单列表
        // 暂时返回空列表，实际项目中需要替换为真实的订单查询逻辑
        List<StoreOrder> orders = new ArrayList<>();

        // 示例：根据时间关键词过滤订单
        String timeKeyword = checkTimeKeyword(keywords);
        if (timeKeyword != null) {
            LocalDate date = LocalDate.now();
            switch (timeKeyword) {
                case "昨天":
                    date = date.minusDays(1);
                    break;
                case "前天":
                    date = date.minusDays(2);
                    break;
                case "今天":
                    // 今天不需要调整
                    break;
                case "明天":
                    date = date.plusDays(1);
                    break;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateStr = date.format(formatter);
            // 这里应该根据日期查询订单
            System.out.println("查询日期：" + dateStr + "的订单");
        }

        return orders;
    }

    /**
     * 搜索商品
     * @param keywords 关键词列表
     * @return List<StoreProduct> 商品列表
     */
    private List<StoreProduct> searchProducts(List<String> keywords) {
        // 这里应该调用商品服务获取商品列表
        // 暂时返回空列表，实际项目中需要替换为真实的商品查询逻辑
        return new ArrayList<>();
    }

    /**
     * 搜索文章
     * @param keywords 关键词列表
     * @return List<Article> 文章列表
     */
    private List<Article> searchArticles(List<String> keywords) {
        // 这里应该调用文章服务获取文章列表
        // 暂时返回空列表，实际项目中需要替换为真实的文章查询逻辑
        return new ArrayList<>();
    }

    /**
     * 检查时间关键词
     * @param keywords 关键词列表
     * @return String 时间关键词
     */
    private String checkTimeKeyword(List<String> keywords) {
        List<String> timeKeywords = new ArrayList<>();
        timeKeywords.add("昨天");
        timeKeywords.add("今天");
        timeKeywords.add("明天");
        timeKeywords.add("前天");

        for (String keyword : keywords) {
            if (timeKeywords.contains(keyword)) {
                return keyword;
            }
        }
        return null;
    }

    /**
     * 生成搜索建议
     * @param keywords 关键词列表
     * @return List<String> 搜索建议
     */
    private List<String> generateSuggestions(List<String> keywords) {
        // 这里应该根据关键词生成搜索建议
        // 暂时返回空列表，实际项目中需要替换为真实的搜索建议生成逻辑
        return new ArrayList<>();
    }
}