package com.zbkj.common.config.mybatis;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单表分表拦截器
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
public class OrderTableInterceptor implements InnerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(OrderTableInterceptor.class);

    // 订单表名前缀
    private static final String ORDER_TABLE_PREFIX = "eb_store_order";
    // 订单详情表名前缀
    private static final String ORDER_INFO_TABLE_PREFIX = "eb_store_order_info";
    // 订单状态表名前缀
    private static final String ORDER_STATUS_TABLE_PREFIX = "eb_store_order_status";

    // 分表数量
    private static final int TABLE_COUNT = 10;

    // 正则表达式匹配表名
    private static final Pattern TABLE_PATTERN = Pattern.compile("(eb_store_order(_info|_status)?)(\\s|\\.)");

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        String sql = boundSql.getSql();
        String newSql = parseSql(sql, parameter);
        if (!sql.equals(newSql)) {
            BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
            MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
            ms.setSqlSource(newMs.getSqlSource());
        }
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        String sql = ms.getBoundSql(parameter).getSql();
        String newSql = parseSql(sql, parameter);
        if (!sql.equals(newSql)) {
            BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), newSql, ms.getBoundSql(parameter).getParameterMappings(), parameter);
            MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
            ms.setSqlSource(newMs.getSqlSource());
        }
    }

    /**
     * 解析SQL，替换表名为分表名
     * @param sql 原始SQL
     * @param parameter 参数
     * @return 解析后的SQL
     */
    private String parseSql(String sql, Object parameter) {
        if (parameter == null) {
            return sql;
        }

        Matcher matcher = TABLE_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String tableName = matcher.group(1);
            String suffix = matcher.group(2);
            String separator = matcher.group(3);

            // 确定分表字段
            String shardingField = getShardingField(tableName);
            if (shardingField == null) {
                matcher.appendReplacement(sb, tableName + separator);
                continue;
            }

            // 获取分表字段值
            Integer shardingValue = getShardingValue(parameter, shardingField);
            if (shardingValue == null) {
                matcher.appendReplacement(sb, tableName + separator);
                continue;
            }

            // 计算分表索引
            int tableIndex = shardingValue % TABLE_COUNT;
            String shardingTableName = tableName + "_" + tableIndex;

            logger.debug("分表路由：{} -> {}，分表字段：{}，值：{}，索引：{}",
                    tableName, shardingTableName, shardingField, shardingValue, tableIndex);

            matcher.appendReplacement(sb, shardingTableName + separator);
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取分表字段
     * @param tableName 表名
     * @return 分表字段名
     */
    private String getShardingField(String tableName) {
        if (ORDER_TABLE_PREFIX.equals(tableName)) {
            return "uid"; // 订单表按用户ID分表
        } else if (ORDER_INFO_TABLE_PREFIX.equals(tableName)) {
            return "order_id"; // 订单详情表按订单ID分表
        } else if (ORDER_STATUS_TABLE_PREFIX.equals(tableName)) {
            return "oid"; // 订单状态表按订单ID分表
        }
        return null;
    }

    /**
     * 获取分表字段值
     * @param parameter 参数
     * @param shardingField 分表字段
     * @return 分表字段值
     */
    private Integer getShardingValue(Object parameter, String shardingField) {
        try {
            // 如果参数是Map类型
            if (parameter instanceof java.util.Map) {
                java.util.Map<?, ?> paramMap = (java.util.Map<?, ?>) parameter;
                Object value = paramMap.get(shardingField);
                if (value instanceof Integer) {
                    return (Integer) value;
                } else if (value instanceof String) {
                    return Integer.parseInt((String) value);
                }
            } else {
                // 如果参数是实体类
                java.lang.reflect.Field field = parameter.getClass().getDeclaredField(shardingField);
                field.setAccessible(true);
                Object value = field.get(parameter);
                if (value instanceof Integer) {
                    return (Integer) value;
                } else if (value instanceof String) {
                    return Integer.parseInt((String) value);
                }
            }
        } catch (Exception e) {
            logger.error("获取分表字段值失败，字段名：{}", shardingField, e);
        }
        return null;
    }

    /**
     * 复制MappedStatement
     * @param ms 原始MappedStatement
     * @param newSqlSource 新的SqlSource
     * @return 复制后的MappedStatement
     */
    private MappedStatement copyFromMappedStatement(MappedStatement ms, org.apache.ibatis.mapping.SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(String.join(StringPool.COMMA, ms.getKeyProperties()));
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    /**
     * BoundSqlSqlSource类
     */
    private static class BoundSqlSqlSource implements org.apache.ibatis.mapping.SqlSource {
        private BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
