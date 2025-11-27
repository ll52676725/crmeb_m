package com.zbkj.common.config.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbkj.common.model.order.StoreOrder;
import com.zbkj.service.dao.order.StoreOrderDao;
import com.zbkj.service.service.order.StoreOrderEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 订单Elasticsearch同步定时任务
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
@Component
public class OrderEsSyncTask {

    private static final Logger logger = LoggerFactory.getLogger(OrderEsSyncTask.class);

    @Autowired
    private StoreOrderDao storeOrderDao;

    @Autowired
    private StoreOrderEsService storeOrderEsService;

    /**
     * 每小时同步一次订单数据到Elasticsearch
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncOrdersToEsHourly() {
        logger.info("开始同步订单数据到Elasticsearch");
        long startTime = System.currentTimeMillis();

        try {
            // 查询最近2小时内更新的订单
            Date twoHoursAgo = new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000);
            List<StoreOrder> orders = storeOrderDao.selectList(
                    new LambdaQueryWrapper<StoreOrder>()
                            .ge(StoreOrder::getUpdateTime, twoHoursAgo)
            );

            if (!CollectionUtils.isEmpty(orders)) {
                storeOrderEsService.syncOrdersToEs(orders);
                logger.info("同步了{}条订单数据到Elasticsearch", orders.size());
            } else {
                logger.info("没有需要同步的订单数据");
            }

            long endTime = System.currentTimeMillis();
            logger.info("订单数据同步完成，耗时{}毫秒", endTime - startTime);
        } catch (Exception e) {
            logger.error("同步订单数据到Elasticsearch失败", e);
        }
    }

    /**
     * 每天凌晨2点重建Elasticsearch索引
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void rebuildOrderEsIndex() {
        logger.info("开始重建订单Elasticsearch索引");
        long startTime = System.currentTimeMillis();

        try {
            storeOrderEsService.rebuildEsIndex();
            long endTime = System.currentTimeMillis();
            logger.info("订单Elasticsearch索引重建完成，耗时{}毫秒", endTime - startTime);
        } catch (Exception e) {
            logger.error("重建订单Elasticsearch索引失败", e);
        }
    }

    /**
     * 每分钟同步一次最近10分钟内创建的订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void syncNewOrdersToEs() {
        try {
            // 查询最近10分钟内创建的订单
            Date tenMinutesAgo = new Date(System.currentTimeMillis() - 10 * 60 * 1000);
            List<StoreOrder> orders = storeOrderDao.selectList(
                    new LambdaQueryWrapper<StoreOrder>()
                            .ge(StoreOrder::getCreateTime, tenMinutesAgo)
            );

            if (!CollectionUtils.isEmpty(orders)) {
                storeOrderEsService.syncOrdersToEs(orders);
                logger.debug("同步了{}条新订单数据到Elasticsearch", orders.size());
            }
        } catch (Exception e) {
            logger.error("同步新订单数据到Elasticsearch失败", e);
        }
    }
}
