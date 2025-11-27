package com.zbkj.common.config;

import com.zbkj.common.utils.ShardingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 分表初始化类
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
@Order(1)
public class ShardingTableInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ShardingTableInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 订单表结构SQL
    private static final String ORDER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `%s` ( `id` int(11) NOT NULL AUTO_INCREMENT, `uid` int(11) NOT NULL DEFAULT '0' COMMENT '用户id', `order_id` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号', `real_name` varchar(50) NOT NULL DEFAULT '' COMMENT '收货人姓名', `user_phone` varchar(20) NOT NULL DEFAULT '' COMMENT '收货人电话', `user_address` varchar(255) NOT NULL DEFAULT '' COMMENT '收货人地址', `cart_id` varchar(255) NOT NULL DEFAULT '' COMMENT '购物车id', `freight_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费', `total_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总价', `pay_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际支付金额', `pay_postage` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '支付邮费', `gain_integral` int(11) NOT NULL DEFAULT '0' COMMENT '获得积分', `use_integral` int(11) NOT NULL DEFAULT '0' COMMENT '使用积分', `integral_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣', `coupon_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券id', `coupon_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券减免', `paid` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1已支付', `pay_time` datetime DEFAULT NULL COMMENT '支付时间', `pay_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '支付方式 0未支付 1微信支付 2支付宝', `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单状态 -1:退款 0:待发货 1:待收货 2:已收货 3:已完成 4:已取消', `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '退款状态 0:未退款 1:申请退款 2:退款中 3:已退款', `refund_reason_wap_explain` varchar(255) NOT NULL DEFAULT '' COMMENT '退款用户说明', `refund_reason_time` datetime DEFAULT NULL COMMENT '退款时间', `refund_reason_wap_img` varchar(255) NOT NULL DEFAULT '' COMMENT '退款用户上传图片', `refund_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '退款备注', `refund_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额', `delivery_name` varchar(255) NOT NULL DEFAULT '' COMMENT '快递公司名称', `delivery_id` varchar(255) NOT NULL DEFAULT '' COMMENT '快递单号', `delivery_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发货类型', `delivery_time` datetime DEFAULT NULL COMMENT '发货时间', `gain_integral_time` datetime DEFAULT NULL COMMENT '获得积分时间', `confirm_time` datetime DEFAULT NULL COMMENT '确认收货时间', `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注', `mark` tinyint(1) NOT NULL DEFAULT '0' COMMENT '订单标记 1删除 0正常', `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除', `unique_key` varchar(32) NOT NULL DEFAULT '' COMMENT '唯一id', `mer_id` int(11) NOT NULL DEFAULT '0' COMMENT '商户id', `is_mer_check` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商户是否已审核', `combination_id` int(11) NOT NULL DEFAULT '0' COMMENT '拼团商品id', `pink_id` int(11) NOT NULL DEFAULT '0' COMMENT '拼团id', `cost` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '成本价', `seckill_id` int(11) NOT NULL DEFAULT '0' COMMENT '秒杀产品ID', `bargain_id` int(11) NOT NULL DEFAULT '0' COMMENT '砍价id', `verify_code` varchar(10) NOT NULL DEFAULT '' COMMENT '核销码', `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '门店id', `shipping_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '配送方式 1快递 2门店自提', `is_remind` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否提醒发货', `is_system_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统删除', PRIMARY KEY (`id`), UNIQUE KEY `order_id` (`order_id`), KEY `user_id` (`uid`), KEY `status` (`status`), KEY `pay_type` (`pay_type`), KEY `add_time` (`add_time`), KEY `mer_id` (`mer_id`), KEY `combination_id` (`combination_id`), KEY `pink_id` (`pink_id`), KEY `seckill_id` (`seckill_id`), KEY `bargain_id` (`bargain_id`), KEY `store_id` (`store_id`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';";

    // 订单详情表结构SQL
    private static final String ORDER_INFO_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `%s` ( `id` int(11) NOT NULL AUTO_INCREMENT, `oid` int(11) NOT NULL DEFAULT '0' COMMENT '订单id', `order_id` varchar(32) NOT NULL DEFAULT '' COMMENT '订单号', `product_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id', `product_info` text COMMENT '商品信息', `cart_info` text COMMENT '购物车信息', `attr_info` text COMMENT '商品属性', `norms_text` varchar(255) NOT NULL DEFAULT '' COMMENT '商品规格', `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品单价', `cost` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品成本价', `num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量', `total_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品总价', `refund_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '退款状态 0未退款 1申请退款 2退款中 3已退款', `refund_num` int(11) NOT NULL DEFAULT '0' COMMENT '退款数量', `refund_reason_wap_explain` varchar(255) NOT NULL DEFAULT '' COMMENT '退款用户说明', `refund_reason_time` datetime DEFAULT NULL COMMENT '退款时间', `refund_reason_wap_img` varchar(255) NOT NULL DEFAULT '' COMMENT '退款用户上传图片', `refund_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '退款备注', `refund_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额', `delivery_name` varchar(255) NOT NULL DEFAULT '' COMMENT '快递公司名称', `delivery_id` varchar(255) NOT NULL DEFAULT '' COMMENT '快递单号', `is_comment` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否评论 0未评论 1已评论', `is_del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除', `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', PRIMARY KEY (`id`), KEY `oid` (`oid`), KEY `order_id` (`order_id`), KEY `product_id` (`product_id`), KEY `is_comment` (`is_comment`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';";

    // 订单状态表结构SQL
    private static final String ORDER_STATUS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `%s` ( `id` int(11) NOT NULL AUTO_INCREMENT, `oid` int(11) NOT NULL DEFAULT '0' COMMENT '订单id', `change_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作类型', `change_message` varchar(255) NOT NULL DEFAULT '' COMMENT '操作信息', `change_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间', `admin_id` int(11) NOT NULL DEFAULT '0' COMMENT '管理员id', `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态', PRIMARY KEY (`id`), KEY `oid` (`oid`), KEY `change_type` (`change_type`), KEY `change_time` (`change_time`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作记录表';";

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化分表");
        long startTime = System.currentTimeMillis();

        try {
            // 初始化订单表分表
            initOrderTables();

            // 初始化订单详情表分表
            initOrderInfoTables();

            // 初始化订单状态表分表
            initOrderStatusTables();

            long endTime = System.currentTimeMillis();
            logger.info("分表初始化完成，耗时{}毫秒", endTime - startTime);
        } catch (Exception e) {
            logger.error("分表初始化失败", e);
            throw e;
        }
    }

    /**
     * 初始化订单表分表
     * @throws SQLException SQL异常
     */
    private void initOrderTables() throws SQLException {
        String[] tableNames = ShardingUtil.getAllShardingTableNames(ShardingUtil.ORDER_TABLE_PREFIX);
        for (String tableName : tableNames) {
            if (!tableExists(tableName)) {
                String sql = String.format(ORDER_TABLE_SQL, tableName);
                jdbcTemplate.execute(sql);
                logger.info("创建订单分表：{}", tableName);
            } else {
                logger.debug("订单分表已存在：{}", tableName);
            }
        }
    }

    /**
     * 初始化订单详情表分表
     * @throws SQLException SQL异常
     */
    private void initOrderInfoTables() throws SQLException {
        String[] tableNames = ShardingUtil.getAllShardingTableNames(ShardingUtil.ORDER_INFO_TABLE_PREFIX);
        for (String tableName : tableNames) {
            if (!tableExists(tableName)) {
                String sql = String.format(ORDER_INFO_TABLE_SQL, tableName);
                jdbcTemplate.execute(sql);
                logger.info("创建订单详情分表：{}", tableName);
            } else {
                logger.debug("订单详情分表已存在：{}", tableName);
            }
        }
    }

    /**
     * 初始化订单状态表分表
     * @throws SQLException SQL异常
     */
    private void initOrderStatusTables() throws SQLException {
        String[] tableNames = ShardingUtil.getAllShardingTableNames(ShardingUtil.ORDER_STATUS_TABLE_PREFIX);
        for (String tableName : tableNames) {
            if (!tableExists(tableName)) {
                String sql = String.format(ORDER_STATUS_TABLE_SQL, tableName);
                jdbcTemplate.execute(sql);
                logger.info("创建订单状态分表：{}", tableName);
            } else {
                logger.debug("订单状态分表已存在：{}", tableName);
            }
        }
    }

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 是否存在
     * @throws SQLException SQL异常
     */
    private boolean tableExists(String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            return resultSet.next();
        }
    }
}
