package com.zbkj.common.constants;

/**
 * Redis常量类
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
public class RedisConstatns {

    /** 用户注册信息缓存Key */
    public static final String USER_REGISTER_KEY = "USER:REGISTER:";

    /** 商品浏览量（每日） */
    public static final String PRO_PAGE_VIEW_KEY = "statistics:product:page_view:";
    public static final String PRO_PRO_PAGE_VIEW_KEY = "statistics:product:pro_page_view:{}:{}";

    /** 商品加购量（每日） */
    public static final String PRO_ADD_CART_KEY = "statistics:product:add_cart:";
    public static final String PRO_PRO_ADD_CART_KEY = "statistics:product:pro_add_cart:{}:{}";

    /** 秒杀活动缓存Key */
    public static final String SECKILL_ACTIVITY_KEY = "seckill:activity:";

    /** 秒杀商品库存缓存Key */
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";

    /** 秒杀商品已售数量缓存Key */
    public static final String SECKILL_SOLD_KEY = "seckill:sold:";

    /** 秒杀用户购买限制缓存Key */
    public static final String SECKILL_USER_LIMIT_KEY = "seckill:user:limit:";

    /** 秒杀订单缓存Key */
    public static final String SECKILL_ORDER_KEY = "seckill:order:";

    /** 秒杀令牌桶缓存Key */
    public static final String SECKILL_TOKEN_BUCKET_KEY = "seckill:token:bucket:";
}
