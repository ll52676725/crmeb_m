package com.zbkj.search.service;

import com.zbkj.search.model.IntentRecognitionResult;

/**
 * 意图识别服务接口
 * @author CRMEB
 * @since 2024-05-20
 */
public interface IntentRecognitionService {

    /**
     * 识别用户意图
     * @param query 用户查询文本
     * @return 意图识别结果
     */
    IntentRecognitionResult recognizeIntent(String query);
}
