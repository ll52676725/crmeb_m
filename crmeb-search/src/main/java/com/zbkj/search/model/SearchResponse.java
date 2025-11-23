package com.zbkj.search.model;

import com.zbkj.common.response.CommonResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 搜索响应结果
 * @author CRMEB
 * @since 2024-05-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SearchResponse extends CommonResult<Object> {

    /** 意图识别结果 */
    private IntentRecognitionResult intentResult;

    /** 搜索结果详情 */
    private Map<String, Object> searchDetails;

    public SearchResponse() {
        super();
    }

    public SearchResponse(Integer code, String msg, Object data, IntentRecognitionResult intentResult, Map<String, Object> searchDetails) {
        super(code, msg, data);
        this.intentResult = intentResult;
        this.searchDetails = searchDetails;
    }

    /**
     * 成功响应
     * @param data 响应数据
     * @param intentResult 意图识别结果
     * @param searchDetails 搜索详情
     * @return 搜索响应结果
     */
    public static SearchResponse success(Object data, IntentRecognitionResult intentResult, Map<String, Object> searchDetails) {
        return new SearchResponse(CommonResult.SUCCESS_CODE, CommonResult.SUCCESS_MSG, data, intentResult, searchDetails);
    }

    /**
     * 失败响应
     * @param msg 失败消息
     * @param intentResult 意图识别结果
     * @return 搜索响应结果
     */
    public static SearchResponse failed(String msg, IntentRecognitionResult intentResult) {
        return new SearchResponse(CommonResult.FAIL_CODE, msg, null, intentResult, null);
    }
}
