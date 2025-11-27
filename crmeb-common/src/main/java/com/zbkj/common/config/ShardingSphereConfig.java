package com.zbkj.common.config;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * ShardingSphere分表配置
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
@Configuration
public class ShardingSphereConfig {

    /**
     * 订单表分表算法
     */
    @Bean
    public PreciseShardingAlgorithm<Integer> orderTableShardingAlgorithm() {
        return new PreciseShardingAlgorithm<Integer>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
                // 根据用户ID进行分表，分10张表
                int tableIndex = shardingValue.getValue() % 10;
                for (String tableName : availableTargetNames) {
                    if (tableName.endsWith(String.valueOf(tableIndex))) {
                        return tableName;
                    }
                }
                throw new IllegalArgumentException("未找到匹配的分表: " + shardingValue);
            }
        };
    }

    /**
     * 订单详情表分表算法
     */
    @Bean
    public PreciseShardingAlgorithm<Integer> orderInfoTableShardingAlgorithm() {
        return new PreciseShardingAlgorithm<Integer>() {
            @Override
            public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {
                // 根据订单ID进行分表，分10张表
                int tableIndex = shardingValue.getValue() % 10;
                for (String tableName : availableTargetNames) {
                    if (tableName.endsWith(String.valueOf(tableIndex))) {
                        return tableName;
                    }
                }
                throw new IllegalArgumentException("未找到匹配的分表: " + shardingValue);
            }
        };
    }
}
