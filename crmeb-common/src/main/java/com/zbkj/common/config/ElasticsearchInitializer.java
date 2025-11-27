package com.zbkj.common.config;

import com.zbkj.common.model.order.StoreOrderEs;
import com.zbkj.service.service.order.StoreOrderEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

/**
 * Elasticsearch初始化类
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
@Order(2)
public class ElasticsearchInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchInitializer.class);

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private StoreOrderEsService storeOrderEsService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化Elasticsearch");
        long startTime = System.currentTimeMillis();

        try {
            // 初始化订单索引
            initOrderIndex();

            long endTime = System.currentTimeMillis();
            logger.info("Elasticsearch初始化完成，耗时{}毫秒", endTime - startTime);
        } catch (Exception e) {
            logger.error("Elasticsearch初始化失败", e);
            // 不抛出异常，继续启动应用
        }
    }

    /**
     * 初始化订单索引
     */
    private void initOrderIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(StoreOrderEs.class);

        // 检查索引是否存在
        if (!indexOperations.exists()) {
            // 创建索引
            boolean created = indexOperations.create();
            if (created) {
                logger.info("创建订单索引成功");
                
                // 创建映射
                boolean mapped = indexOperations.putMapping();
                if (mapped) {
                    logger.info("创建订单索引映射成功");
                    
                    // 初始化数据
                    logger.info("开始初始化订单索引数据");
                    storeOrderEsService.rebuildEsIndex();
                    logger.info("订单索引数据初始化完成");
                } else {
                    logger.error("创建订单索引映射失败");
                }
            } else {
                logger.error("创建订单索引失败");
            }
        } else {
            logger.debug("订单索引已存在");
            
            // 检查映射是否存在
            boolean mappingExists = indexOperations.existsMapping();
            if (!mappingExists) {
                boolean mapped = indexOperations.putMapping();
                if (mapped) {
                    logger.info("创建订单索引映射成功");
                } else {
                    logger.error("创建订单索引映射失败");
                }
            }
        }
    }
}
