package com.zbkj.search.service.impl;

import com.zbkj.common.response.CommonResult;
import com.zbkj.search.model.IntentRecognitionResult;
import com.zbkj.search.service.IntentRecognitionService;
import com.zbkj.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * SearchServiceImpl测试类
 * @author CRMEB
 * @since 2024-05-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    /**
     * 测试意图识别
     */
    @Test
    public void testIntentRecognition() {
        // 测试查询订单状态
        IntentRecognitionResult result1 = intentRecognitionService.recognizeIntent("我的订单状态");
        assertEquals("ORDER_STATUS", result1.getIntentType());
        assertNotNull(result1.getConfidence());
        assertTrue(result1.getConfidence() > 0.5);

        // 测试查询物流信息
        IntentRecognitionResult result2 = intentRecognitionService.recognizeIntent("我的订单物流信息");
        assertEquals("LOGISTICS_INFO", result2.getIntentType());
        assertNotNull(result2.getConfidence());
        assertTrue(result2.getConfidence() > 0.5);

        // 测试查询退款状态
        IntentRecognitionResult result3 = intentRecognitionService.recognizeIntent("我的订单退款状态");
        assertEquals("REFUND_STATUS", result3.getIntentType());
        assertNotNull(result3.getConfidence());
        assertTrue(result3.getConfidence() > 0.5);

        // 测试查询待支付订单
        IntentRecognitionResult result4 = intentRecognitionService.recognizeIntent("我的待支付订单");
        assertEquals("UNPAID_ORDERS", result4.getIntentType());
        assertNotNull(result4.getConfidence());
        assertTrue(result4.getConfidence() > 0.5);

        // 测试查询待发货订单
        IntentRecognitionResult result5 = intentRecognitionService.recognizeIntent("我的待发货订单");
        assertEquals("UNSHIPPED_ORDERS", result5.getIntentType());
        assertNotNull(result5.getConfidence());
        assertTrue(result5.getConfidence() > 0.5);

        // 测试查询待收货订单
        IntentRecognitionResult result6 = intentRecognitionService.recognizeIntent("我的待收货订单");
        assertEquals("UNRECEIVED_ORDERS", result6.getIntentType());
        assertNotNull(result6.getConfidence());
        assertTrue(result6.getConfidence() > 0.5);

        // 测试查询已完成订单
        IntentRecognitionResult result7 = intentRecognitionService.recognizeIntent("我的已完成订单");
        assertEquals("COMPLETED_ORDERS", result7.getIntentType());
        assertNotNull(result7.getConfidence());
        assertTrue(result7.getConfidence() > 0.5);

        // 测试查询已取消订单
        IntentRecognitionResult result8 = intentRecognitionService.recognizeIntent("我的已取消订单");
        assertEquals("CANCELLED_ORDERS", result8.getIntentType());
        assertNotNull(result8.getConfidence());
        assertTrue(result8.getConfidence() > 0.5);
    }

    /**
     * 测试智能搜索
     */
    @Test
    public void testHandleSearchRequest() {
        // 测试查询订单状态
        CommonResult<Object> result1 = searchService.handleSearchRequest(1, "我的订单状态");
        assertEquals(200, result1.getCode());
        assertNotNull(result1.getData());

        // 测试查询待支付订单
        CommonResult<Object> result2 = searchService.handleSearchRequest(1, "我的待支付订单");
        assertEquals(200, result2.getCode());
        assertNotNull(result2.getData());

        // 测试查询待发货订单
        CommonResult<Object> result3 = searchService.handleSearchRequest(1, "我的待发货订单");
        assertEquals(200, result3.getCode());
        assertNotNull(result3.getData());

        // 测试查询待收货订单
        CommonResult<Object> result4 = searchService.handleSearchRequest(1, "我的待收货订单");
        assertEquals(200, result4.getCode());
        assertNotNull(result4.getData());

        // 测试查询已完成订单
        CommonResult<Object> result5 = searchService.handleSearchRequest(1, "我的已完成订单");
        assertEquals(200, result5.getCode());
        assertNotNull(result5.getData());

        // 测试查询已取消订单
        CommonResult<Object> result6 = searchService.handleSearchRequest(1, "我的已取消订单");
        assertEquals(200, result6.getCode());
        assertNotNull(result6.getData());
    }
}
