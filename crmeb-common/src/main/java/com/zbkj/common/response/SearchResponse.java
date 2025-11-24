package com.zbkj.common.response;

import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.product.StoreProduct;
import com.zbkj.common.model.article.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索结果响应对象
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SearchResponse对象", description="搜索结果响应对象")
public class SearchResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单列表")
    private List<StoreOrder> orderList;

    @ApiModelProperty(value = "商品列表")
    private List<StoreProduct> productList;

    @ApiModelProperty(value = "文章列表")
    private List<Article> articleList;

    @ApiModelProperty(value = "搜索意图")
    private String intent;

    @ApiModelProperty(value = "搜索建议")
    private List<String> suggestions;
}