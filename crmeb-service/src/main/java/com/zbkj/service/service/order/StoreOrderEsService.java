package com.zbkj.service.service.order;

import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

/**
 * 订单Elasticsearch服务接口
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
public interface StoreOrderEsService {

    /**
     * 将订单同步到Elasticsearch
     * @param order 订单实体
     * @return 同步后的订单文档
     */
    StoreOrderEs syncOrderToEs(StoreOrder order);

    /**
     * 批量将订单同步到Elasticsearch
     * @param orders 订单实体列表
     * @return 同步后的订单文档列表
     */
    List<StoreOrderEs> syncOrdersToEs(List<StoreOrder> orders);

    /**
     * 根据ID从Elasticsearch中获取订单
     * @param id 订单ID
     * @return 订单文档
     */
    Optional<StoreOrderEs> getOrderFromEs(Integer id);

    /**
     * 根据ID从Elasticsearch中删除订单
     * @param id 订单ID
     */
    void deleteOrderFromEs(Integer id);

    /**
     * 根据用户ID从Elasticsearch中查询订单
     * @param uid 用户ID
     * @return 订单文档列表
     */
    List<StoreOrderEs> findOrdersByUidFromEs(Integer uid);

    /**
     * 根据订单状态从Elasticsearch中查询订单
     * @param status 订单状态
     * @return 订单文档列表
     */
    List<StoreOrderEs> findOrdersByStatusFromEs(Integer status);

    /**
     * 根据用户ID和订单状态从Elasticsearch中查询订单
     * @param uid 用户ID
     * @param status 订单状态
     * @return 订单文档列表
     */
    List<StoreOrderEs> findOrdersByUidAndStatusFromEs(Integer uid, Integer status);

    /**
     * 根据订单号从Elasticsearch中查询订单
     * @param orderId 订单号
     * @return 订单文档
     */
    StoreOrderEs findOrderByOrderIdFromEs(String orderId);

    /**
     * 高级搜索订单
     * @param queryBuilder 查询构建器
     * @param pageable 分页参数
     * @return 分页订单文档列表
     */
    Page<StoreOrderEs> searchOrders(NativeSearchQueryBuilder queryBuilder, Pageable pageable);

    /**
     * 搜索订单（支持关键词搜索）
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 分页订单文档列表
     */
    Page<StoreOrderEs> searchOrdersByKeyword(String keyword, Pageable pageable);

    /**
     * 初始化Elasticsearch索引
     */
    void initEsIndex();

    /**
     * 重建Elasticsearch索引
     */
    void rebuildEsIndex();
}
