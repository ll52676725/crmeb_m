package com.zbkj.search.model;

import com.zbkj.search.enums.IntentTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * 意图识别结果
 * @author CRMEB
 * @since 2024-05-20
 */
@Data
public class IntentRecognitionResult {

    /** 意图类型 */
    private IntentTypeEnum intentType;
    
    /** 意图置信度 */
    private double confidence;
    
    /** 提取的实体信息 */
    private Map<String, Object> entities;
    
    /** 原始查询文本 */
    private String originalText;

    public IntentRecognitionResult() {
    }

    public IntentRecognitionResult(IntentTypeEnum intentType, double confidence, Map<String, Object> entities, String originalText) {
        this.intentType = intentType;
        this.confidence = confidence;
        this.entities = entities;
        this.originalText = originalText;
    }
}
