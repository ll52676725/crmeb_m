package com.zbkj.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分表工具类
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
public class ShardingUtil {

    private static final Logger logger = LoggerFactory.getLogger(ShardingUtil.class);

    // 订单表名前缀
    public static final String ORDER_TABLE_PREFIX = "eb_store_order";
    // 订单详情表名前缀
    public static final String ORDER_INFO_TABLE_PREFIX = "eb_store_order_info";
    // 订单状态表名前缀
    public static final String ORDER_STATUS_TABLE_PREFIX = "eb_store_order_status";

    // 分表数量
    private static final int TABLE_COUNT = 10;

    /**
     * 获取订单表分表名
     * @param uid 用户ID
     * @return 分表名
     */
    public static String getOrderTableName(Integer uid) {
        if (uid == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        int tableIndex = uid % TABLE_COUNT;
        String tableName = ORDER_TABLE_PREFIX + "_" + tableIndex;
        logger.debug("订单表分表：uid={} -> table={}", uid, tableName);
        return tableName;
    }

    /**
     * 获取订单详情表分表名
     * @param orderId 订单ID
     * @return 分表名
     */
    public static String getOrderInfoTableName(Integer orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        int tableIndex = orderId % TABLE_COUNT;
        String tableName = ORDER_INFO_TABLE_PREFIX + "_" + tableIndex;
        logger.debug("订单详情表分表：orderId={} -> table={}", orderId, tableName);
        return tableName;
    }

    /**
     * 获取订单状态表分表名
     * @param oid 订单ID
     * @return 分表名
     */
    public static String getOrderStatusTableName(Integer oid) {
        if (oid == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        int tableIndex = oid % TABLE_COUNT;
        String tableName = ORDER_STATUS_TABLE_PREFIX + "_" + tableIndex;
        logger.debug("订单状态表分表：oid={} -> table={}", oid, tableName);
        return tableName;
    }

    /**
     * 根据表名前缀和分表值获取分表名
     * @param tablePrefix 表名前缀
     * @param shardingValue 分表值
     * @return 分表名
     */
    public static String getShardingTableName(String tablePrefix, Integer shardingValue) {
        if (tablePrefix == null || tablePrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("表名前缀不能为空");
        }
        if (shardingValue == null) {
            throw new IllegalArgumentException("分表值不能为空");
        }
        int tableIndex = shardingValue % TABLE_COUNT;
        String tableName = tablePrefix + "_" + tableIndex;
        logger.debug("分表：prefix={}, value={} -> table={}", tablePrefix, shardingValue, tableName);
        return tableName;
    }

    /**
     * 获取所有分表名
     * @param tablePrefix 表名前缀
     * @return 所有分表名列表
     */
    public static String[] getAllShardingTableNames(String tablePrefix) {
        if (tablePrefix == null || tablePrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("表名前缀不能为空");
        }
        String[] tableNames = new String[TABLE_COUNT];
        for (int i = 0; i < TABLE_COUNT; i++) {
            tableNames[i] = tablePrefix + "_" + i;
        }
        return tableNames;
    }

    /**
     * 获取分表数量
     * @return 分表数量
     */
    public static int getTableCount() {
        return TABLE_COUNT;
    }
}
