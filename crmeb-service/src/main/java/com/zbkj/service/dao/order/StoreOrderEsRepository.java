package com.zbkj.service.dao.order;

import com.zbkj.common.model.order.StoreOrderEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单Elasticsearch Repository
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
@Repository
public interface StoreOrderEsRepository extends ElasticsearchRepository<StoreOrderEs, Integer> {

    /**
     * 根据用户ID查询订单
     * @param uid 用户ID
     * @return 订单列表
     */
    List<StoreOrderEs> findByUid(Integer uid);

    /**
     * 根据订单状态查询订单
     * @param status 订单状态
     * @return 订单列表
     */
    List<StoreOrderEs> findByStatus(Integer status);

    /**
     * 根据用户ID和订单状态查询订单
     * @param uid 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<StoreOrderEs> findByUidAndStatus(Integer uid, Integer status);

    /**
     * 根据订单号查询订单
     * @param orderId 订单号
     * @return 订单
     */
    StoreOrderEs findByOrderId(String orderId);

    /**
     * 根据支付状态查询订单
     * @param paid 支付状态
     * @return 订单列表
     */
    List<StoreOrderEs> findByPaid(Boolean paid);

    /**
     * 根据退款状态查询订单
     * @param refundStatus 退款状态
     * @return 订单列表
     */
    List<StoreOrderEs> findByRefundStatus(Integer refundStatus);
}
