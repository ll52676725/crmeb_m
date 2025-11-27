package com.zbkj.test;

import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderEs;
import com.zbkj.service.service.order.StoreOrderEsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * Elasticsearch功能测试
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
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    private StoreOrderEsService storeOrderEsService;

    @Test
    public void testInitIndex() {
        // 测试初始化索引
        storeOrderEsService.initEsIndex();
        System.out.println("索引初始化完成");
    }

    @Test
    public void testRebuildIndex() {
        // 测试重建索引
        storeOrderEsService.rebuildEsIndex();
        System.out.println("索引重建完成");
    }

    @Test
    public void testSyncOrder() {
        // 测试同步单个订单
        StoreOrder order = new StoreOrder();
        order.setId(1);
        order.setUid(1);
        order.setOrderId("TEST_ES_001");
        order.setRealName("测试用户");
        order.setUserPhone("13800138000");
        order.setUserAddress("北京市朝阳区");
        order.setTotalPrice(100.00);
        order.setPayPrice(100.00);
        order.setPaid(1);
        order.setStatus(1);
        
        StoreOrderEs orderEs = storeOrderEsService.syncOrderToEs(order);
        System.out.printf("同步订单完成，ID=%d%n", orderEs.getId());
    }

    @Test
    public void testGetOrder() {
        // 测试获取订单
        Optional<StoreOrderEs> orderOptional = storeOrderEsService.getOrderFromEs(1);
        if (orderOptional.isPresent()) {
            StoreOrderEs orderEs = orderOptional.get();
            System.out.printf("获取订单成功，订单号=%s%n", orderEs.getOrderId());
        } else {
            System.out.println("订单不存在");
        }
    }

    @Test
    public void testSearchByKeyword() {
        // 测试关键词搜索
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<StoreOrderEs> orders = storeOrderEsService.searchOrdersByKeyword("测试", pageable);
        System.out.printf("关键词搜索结果，总数=%d，当前页数量=%d%n",
                orders.getTotalElements(), orders.getContent().size());
    }

    @Test
    public void testSearchByUid() {
        // 测试按用户ID搜索
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByUidFromEs(1);
        System.out.printf("按用户ID搜索结果，数量=%d%n", orders.size());
    }

    @Test
    public void testSearchByStatus() {
        // 测试按状态搜索
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByStatusFromEs(1);
        System.out.printf("按状态搜索结果，数量=%d%n", orders.size());
    }

    @Test
    public void testSearchByUidAndStatus() {
        // 测试按用户ID和状态搜索
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByUidAndStatusFromEs(1, 1);
        System.out.printf("按用户ID和状态搜索结果，数量=%d%n", orders.size());
    }

    @Test
    public void testSearchByOrderId() {
        // 测试按订单号搜索
        StoreOrderEs order = storeOrderEsService.findOrderByOrderIdFromEs("TEST_ES_001");
        if (order != null) {
            System.out.printf("按订单号搜索成功，订单ID=%d%n", order.getId());
        } else {
            System.out.println("订单不存在");
        }
    }

    @Test
    public void testDeleteOrder() {
        // 测试删除订单
        storeOrderEsService.deleteOrderFromEs(1);
        System.out.println("删除订单完成");
    }
}
