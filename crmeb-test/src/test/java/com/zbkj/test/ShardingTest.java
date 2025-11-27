package com.zbkj.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.common.model.order.StoreOrderInfo;
import com.zbkj.common.model.order.StoreOrderStatus;
import com.zbkj.common.utils.ShardingUtil;
import com.zbkj.service.dao.order.StoreOrderDao;
import com.zbkj.service.dao.order.StoreOrderInfoDao;
import com.zbkj.service.dao.order.StoreOrderStatusDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * 分表功能测试
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
public class ShardingTest {

    @Autowired
    private StoreOrderDao storeOrderDao;

    @Autowired
    private StoreOrderInfoDao storeOrderInfoDao;

    @Autowired
    private StoreOrderStatusDao storeOrderStatusDao;

    @Test
    public void testShardingUtil() {
        // 测试分表工具类
        for (int i = 0; i < 20; i++) {
            String orderTable = ShardingUtil.getOrderTableName(i);
            String orderInfoTable = ShardingUtil.getOrderInfoTableName(i);
            String orderStatusTable = ShardingUtil.getOrderStatusTableName(i);
            System.out.printf("uid=%d -> orderTable=%s, orderInfoTable=%s, orderStatusTable=%s%n",
                    i, orderTable, orderInfoTable, orderStatusTable);
        }
    }

    @Test
    public void testInsertOrder() {
        // 测试插入订单数据
        for (int i = 1; i <= 10; i++) {
            StoreOrder order = new StoreOrder();
            order.setUid(i);
            order.setOrderId("TEST" + System.currentTimeMillis() + i);
            order.setRealName("测试用户" + i);
            order.setUserPhone("1380013800" + i);
            order.setUserAddress("测试地址" + i);
            order.setTotalPrice(100.00);
            order.setPayPrice(100.00);
            order.setPaid(0);
            order.setStatus(0);
            order.setAddTime(new Date());
            
            int result = storeOrderDao.insert(order);
            System.out.printf("插入订单，uid=%d，结果=%d%n", i, result);
        }
    }

    @Test
    public void testSelectOrder() {
        // 测试查询订单数据
        for (int i = 1; i <= 10; i++) {
            List<StoreOrder> orders = storeOrderDao.selectList(
                    new LambdaQueryWrapper<StoreOrder>()
                            .eq(StoreOrder::getUid, i)
            );
            System.out.printf("查询uid=%d的订单，数量=%d%n", i, orders.size());
        }
    }

    @Test
    public void testPageOrder() {
        // 测试分页查询订单数据
        Page<StoreOrder> page = new Page<>(1, 10);
        IPage<StoreOrder> orderPage = storeOrderDao.selectPage(page, null);
        System.out.printf("分页查询订单，总数=%d，当前页数量=%d%n",
                orderPage.getTotal(), orderPage.getRecords().size());
    }

    @Test
    public void testInsertOrderInfo() {
        // 测试插入订单详情数据
        for (int i = 1; i <= 10; i++) {
            StoreOrderInfo orderInfo = new StoreOrderInfo();
            orderInfo.setOid(i);
            orderInfo.setOrderId("TEST" + System.currentTimeMillis() + i);
            orderInfo.setProductId(i);
            orderInfo.setNormsText("测试规格" + i);
            orderInfo.setPrice(100.00);
            orderInfo.setNum(1);
            orderInfo.setTotalPrice(100.00);
            orderInfo.setAddTime(new Date());
            
            int result = storeOrderInfoDao.insert(orderInfo);
            System.out.printf("插入订单详情，oid=%d，结果=%d%n", i, result);
        }
    }

    @Test
    public void testInsertOrderStatus() {
        // 测试插入订单状态数据
        for (int i = 1; i <= 10; i++) {
            StoreOrderStatus orderStatus = new StoreOrderStatus();
            orderStatus.setOid(i);
            orderStatus.setChangeType(1);
            orderStatus.setChangeMessage("测试操作" + i);
            orderStatus.setChangeTime(new Date());
            
            int result = storeOrderStatusDao.insert(orderStatus);
            System.out.printf("插入订单状态，oid=%d，结果=%d%n", i, result);
        }
    }
}
