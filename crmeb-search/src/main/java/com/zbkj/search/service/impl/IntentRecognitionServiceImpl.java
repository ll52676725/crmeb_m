package com.zbkj.search.service.impl;

import com.zbkj.search.enums.IntentTypeEnum;
import com.zbkj.search.model.IntentRecognitionResult;
import com.zbkj.search.service.IntentRecognitionService;
import com.zbkj.search.utils.NLPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意图识别服务实现类
 * @author CRMEB
 * @since 2024-05-20
 */
@Service
public class IntentRecognitionServiceImpl implements IntentRecognitionService {

    @Autowired
    private NLPUtils nlpUtils;

    /**
     * 识别用户意图
     * @param query 用户查询文本
     * @return 意图识别结果
     */
    @Override
    public IntentRecognitionResult recognizeIntent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new IntentRecognitionResult(IntentTypeEnum.OTHER, 0.0, new HashMap<>(), query);
        }

        String lowerQuery = query.toLowerCase().trim();
        List<String> keywords = nlpUtils.extractKeywords(lowerQuery, 10);
        Map<String, Object> entities = new HashMap<>();

        // 提取实体信息
        String orderNumber = nlpUtils.extractOrderNumber(lowerQuery);
        if (orderNumber != null) {
            entities.put("orderNumber", orderNumber);
        }

        List<String> timeEntities = nlpUtils.extractTime(lowerQuery);
        if (!timeEntities.isEmpty()) {
            entities.put("time", timeEntities);
        }

        List<String> numberEntities = nlpUtils.extractNumbers(lowerQuery);
        if (!numberEntities.isEmpty()) {
            entities.put("numbers", numberEntities);
        }

        // 意图识别逻辑
        IntentTypeEnum intentType = IntentTypeEnum.OTHER;
        double confidence = 0.0;

        // 查询订单状态
        if (containsAny(lowerQuery, "订单", "状态") && containsAny(lowerQuery, "查", "看看", "查询", "了解")) {
            intentType = IntentTypeEnum.QUERY_ORDER_STATUS;
            confidence = 0.8;
        }

        // 查询物流信息
        if (containsAny(lowerQuery, "物流", "快递", "配送", "发货", "单号", "跟踪")) {
            intentType = IntentTypeEnum.QUERY_LOGISTICS;
            confidence = 0.85;
        }

        // 查询退款状态
        if (containsAny(lowerQuery, "退款", "退货", "退款状态", "退货状态")) {
            intentType = IntentTypeEnum.QUERY_REFUND_STATUS;
            confidence = 0.85;
        }

        // 查询待支付订单
        if (containsAny(lowerQuery, "待支付", "未支付", "还没付", "没付款")) {
            intentType = IntentTypeEnum.QUERY_UNPAID_ORDERS;
            confidence = 0.9;
        }

        // 查询待发货订单
        if (containsAny(lowerQuery, "待发货", "未发货", "还没发", "没发货")) {
            intentType = IntentTypeEnum.QUERY_UNSHIPPED_ORDERS;
            confidence = 0.9;
        }

        // 查询待收货订单
        if (containsAny(lowerQuery, "待收货", "未收货", "还没收到", "没收到", "派送中")) {
            intentType = IntentTypeEnum.QUERY_UNRECEIVED_ORDERS;
            confidence = 0.9;
        }

        // 查询已完成订单
        if (containsAny(lowerQuery, "已完成", "完成", "已收货", "收到货")) {
            intentType = IntentTypeEnum.QUERY_COMPLETED_ORDERS;
            confidence = 0.9;
        }

        // 查询已取消订单
        if (containsAny(lowerQuery, "已取消", "取消", "作废", "取消订单")) {
            intentType = IntentTypeEnum.QUERY_CANCELLED_ORDERS;
            confidence = 0.9;
        }

        // 如果有订单号，提高置信度
        if (orderNumber != null) {
            confidence += 0.1;
        }

        // 确保置信度不超过1.0
        confidence = Math.min(confidence, 1.0);

        return new IntentRecognitionResult(intentType, confidence, entities, query);
    }

    /**
     * 判断字符串是否包含指定列表中的任何一个字符串
     * @param str 字符串
     * @param keywords 关键词列表
     * @return 是否包含
     */
    private boolean containsAny(String str, String... keywords) {
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
