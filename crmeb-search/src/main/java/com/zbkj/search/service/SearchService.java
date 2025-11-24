package com.zbkj.search.service;

import com.zbkj.common.response.SearchResponse;

/**
 * 智能搜索服务接口
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
public interface SearchService {

    /**
     * 智能搜索
     * @param keyword 搜索关键词
     * @param uid 用户ID
     * @return SearchResponse 搜索结果
     */
    SearchResponse intelligentSearch(String keyword, Integer uid);
}