package com.zbkj.service.service.impl.order;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderEs;
import com.zbkj.service.dao.order.StoreOrderDao;
import com.zbkj.service.dao.order.StoreOrderEsRepository;
import com.zbkj.service.service.order.StoreOrderEsService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 订单Elasticsearch服务实现类
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
public class StoreOrderEsServiceImpl implements StoreOrderEsService {

    @Resource
    private StoreOrderEsRepository storeOrderEsRepository;

    @Resource
    private StoreOrderDao storeOrderDao;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public StoreOrderEs syncOrderToEs(StoreOrder order) {
        if (order == null) {
            return null;
        }

        StoreOrderEs orderEs = new StoreOrderEs();
        BeanUtils.copyProperties(order, orderEs);
        return storeOrderEsRepository.save(orderEs);
    }

    @Override
    public List<StoreOrderEs> syncOrdersToEs(List<StoreOrder> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }

        List<StoreOrderEs> orderEsList = new ArrayList<>();
        for (StoreOrder order : orders) {
            StoreOrderEs orderEs = new StoreOrderEs();
            BeanUtils.copyProperties(order, orderEs);
            orderEsList.add(orderEs);
        }

        return (List<StoreOrderEs>) storeOrderEsRepository.saveAll(orderEsList);
    }

    @Override
    public Optional<StoreOrderEs> getOrderFromEs(Integer id) {
        return storeOrderEsRepository.findById(id);
    }

    @Override
    public void deleteOrderFromEs(Integer id) {
        storeOrderEsRepository.deleteById(id);
    }

    @Override
    public List<StoreOrderEs> findOrdersByUidFromEs(Integer uid) {
        return storeOrderEsRepository.findByUid(uid);
    }

    @Override
    public List<StoreOrderEs> findOrdersByStatusFromEs(Integer status) {
        return storeOrderEsRepository.findByStatus(status);
    }

    @Override
    public List<StoreOrderEs> findOrdersByUidAndStatusFromEs(Integer uid, Integer status) {
        return storeOrderEsRepository.findByUidAndStatus(uid, status);
    }

    @Override
    public StoreOrderEs findOrderByOrderIdFromEs(String orderId) {
        return storeOrderEsRepository.findByOrderId(orderId);
    }

    @Override
    public org.springframework.data.domain.Page<StoreOrderEs> searchOrders(NativeSearchQueryBuilder queryBuilder, Pageable pageable) {
        queryBuilder.withPageable(pageable);
        return elasticsearchRestTemplate.queryForPage(queryBuilder.build(), StoreOrderEs.class);
    }

    @Override
    public org.springframework.data.domain.Page<StoreOrderEs> searchOrdersByKeyword(String keyword, Pageable pageable) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(keyword)
                    .field("orderId")
                    .field("realName")
                    .field("userPhone")
                    .field("userAddress")
                    .field("deliveryName")
                    .field("deliveryId")
                    .field("remark")
                    .field("mark");
            boolQuery.must(multiMatchQuery);
        }

        // 构建查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable);

        return elasticsearchRestTemplate.queryForPage(queryBuilder.build(), StoreOrderEs.class);
    }

    @Override
    public void initEsIndex() {
        // 检查索引是否存在，如果不存在则创建
        if (!elasticsearchRestTemplate.indexExists(StoreOrderEs.class)) {
            elasticsearchRestTemplate.createIndex(StoreOrderEs.class);
            elasticsearchRestTemplate.putMapping(StoreOrderEs.class);
        }
    }

    @Override
    public void rebuildEsIndex() {
        // 初始化索引
        initEsIndex();

        // 分页查询所有订单并同步到Elasticsearch
        int pageSize = 1000;
        int pageNum = 1;
        
        while (true) {
            IPage<StoreOrder> orderPage = storeOrderDao.selectPage(
                    new Page<>(pageNum, pageSize),
                    new LambdaQueryWrapper<StoreOrder>()
            );

            List<StoreOrder> orders = orderPage.getRecords();
            if (CollectionUtils.isEmpty(orders)) {
                break;
            }

            // 同步到Elasticsearch
            syncOrdersToEs(orders);

            // 如果是最后一页，退出循环
            if (orderPage.getCurrent() >= orderPage.getPages()) {
                break;
            }

            pageNum++;
        }
    }
}
