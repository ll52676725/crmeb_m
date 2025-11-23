package com.zbkj.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.zbkj.common.response.CommonResult;
import com.zbkj.common.response.PageResult;
import com.zbkj.service.service.order.OrderService;
import com.zbkj.service.service.order.StoreOrderService;
import com.zbkj.search.enums.IntentTypeEnum;
import com.zbkj.search.model.IntentRecognitionResult;
import com.zbkj.search.service.IntentRecognitionService;
import com.zbkj.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 搜索服务实现类
 * @author CRMEB
 * @since 2024-05-20
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private IntentRecognitionService intentRecognitionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreOrderService storeOrderService;

    /**
     * 处理用户搜索请求
     * @param userId 用户ID
     * @param query 搜索查询
     * @return 搜索结果
     */
    @Override
    public CommonResult<Object> handleSearchRequest(Integer userId, String query) {
        // 意图识别
        IntentRecognitionResult intentResult = intentRecognitionService.recognizeIntent(query);

        // 根据意图执行搜索
        return executeSearchByIntent(userId, intentResult);
    }

    /**
     * 根据意图执行搜索
     * @param userId 用户ID
     * @param intentResult 意图识别结果
     * @return 搜索结果
     */
    @Override
    public CommonResult<Object> executeSearchByIntent(Integer userId, IntentRecognitionResult intentResult) {
        IntentTypeEnum intentType = intentResult.getIntentType();
        Map<String, Object> entities = intentResult.getEntities();

        switch (intentType) {
            case QUERY_ORDER_STATUS:
                return handleQueryOrderStatus(userId, entities);
            case QUERY_LOGISTICS:
                return handleQueryLogistics(userId, entities);
            case QUERY_REFUND_STATUS:
                return handleQueryRefundStatus(userId, entities);
            case QUERY_UNPAID_ORDERS:
                return handleQueryUnpaidOrders(userId);
            case QUERY_UNSHIPPED_ORDERS:
                return handleQueryUnshippedOrders(userId);
            case QUERY_UNRECEIVED_ORDERS:
                return handleQueryUnreceivedOrders(userId);
            case QUERY_COMPLETED_ORDERS:
                return handleQueryCompletedOrders(userId);
            case QUERY_CANCELLED_ORDERS:
                return handleQueryCancelledOrders(userId);
            default:
                return CommonResult.failed("无法理解您的查询，请尝试其他关键词");
        }
    }

    /**
     * 处理查询订单状态
     * @param userId 用户ID
     * @param entities 实体信息
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryOrderStatus(Integer userId, Map<String, Object> entities) {
        String orderNumber = (String) entities.get("orderNumber");
        if (orderNumber != null) {
            // 根据订单号查询订单状态
            try {
                Object order = storeOrderService.getByOrderId(orderNumber);
                if (order != null) {
                    return CommonResult.success(order, "查询成功");
                } else {
                    return CommonResult.failed("未找到该订单");
                }
            } catch (Exception e) {
                return CommonResult.failed("查询订单状态失败: " + e.getMessage());
            }
        } else {
            // 查询所有订单状态统计
            try {
                Object orderStatusNum = storeOrderService.getOrderStatusNum(userId);
                return CommonResult.success(orderStatusNum, "查询成功");
            } catch (Exception e) {
                return CommonResult.failed("查询订单状态统计失败: " + e.getMessage());
            }
        }
    }

    /**
     * 处理查询物流信息
     * @param userId 用户ID
     * @param entities 实体信息
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryLogistics(Integer userId, Map<String, Object> entities) {
        String orderNumber = (String) entities.get("orderNumber");
        if (orderNumber != null) {
            try {
                Object logisticsInfo = storeOrderService.getLogisticsInfo(orderNumber);
                return CommonResult.success(logisticsInfo, "查询成功");
            } catch (Exception e) {
                return CommonResult.failed("查询物流信息失败: " + e.getMessage());
            }
        } else {
            return CommonResult.failed("请提供订单号以查询物流信息");
        }
    }

    /**
     * 处理查询退款状态
     * @param userId 用户ID
     * @param entities 实体信息
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryRefundStatus(Integer userId, Map<String, Object> entities) {
        String orderNumber = (String) entities.get("orderNumber");
        if (orderNumber != null) {
            try {
                Object order = storeOrderService.getByOrderId(orderNumber);
                if (order != null) {
                    // 这里假设订单对象中有refundStatus字段
                    Map<String, Object> result = new HashMap<>();
                    result.put("orderNumber", orderNumber);
                    result.put("refundStatus", getRefundStatus(order));
                    return CommonResult.success(result, "查询成功");
                } else {
                    return CommonResult.failed("未找到该订单");
                }
            } catch (Exception e) {
                return CommonResult.failed("查询退款状态失败: " + e.getMessage());
            }
        } else {
            return CommonResult.failed("请提供订单号以查询退款状态");
        }
    }

    /**
     * 处理查询待支付订单
     * @param userId 用户ID
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryUnpaidOrders(Integer userId) {
        try {
            Object unpaidOrders = orderService.getUserCurrentDayUnpaidOrders(userId);
            return CommonResult.success(unpaidOrders, "查询成功");
        } catch (Exception e) {
            return CommonResult.failed("查询待支付订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理查询待发货订单
     * @param userId 用户ID
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryUnshippedOrders(Integer userId) {
        try {
            Object unshippedOrders = orderService.getUserCurrentDayUnshippedOrders(userId);
            return CommonResult.success(unshippedOrders, "查询成功");
        } catch (Exception e) {
            return CommonResult.failed("查询待发货订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理查询待收货订单
     * @param userId 用户ID
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryUnreceivedOrders(Integer userId) {
        try {
            Object unreceivedOrders = orderService.getUserCurrentDayUnreceivedOrders(userId);
            return CommonResult.success(unreceivedOrders, "查询成功");
        } catch (Exception e) {
            return CommonResult.failed("查询待收货订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理查询已完成订单
     * @param userId 用户ID
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryCompletedOrders(Integer userId) {
        try {
            Object completedOrders = orderService.getUserCurrentDayReceivedOrders(userId);
            return CommonResult.success(completedOrders, "查询成功");
        } catch (Exception e) {
            return CommonResult.failed("查询已完成订单失败: " + e.getMessage());
        }
    }

    /**
     * 处理查询已取消订单
     * @param userId 用户ID
     * @return 搜索结果
     */
    private CommonResult<Object> handleQueryCancelledOrders(Integer userId) {
        try {
            Object cancelledOrders = orderService.getUserCurrentDayCancelledOrders(userId);
            return CommonResult.success(cancelledOrders, "查询成功");
        } catch (Exception e) {
            return CommonResult.failed("查询已取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取退款状态（需要根据实际订单对象结构实现）
     * @param order 订单对象
     * @return 退款状态
     */
    private String getRefundStatus(Object order) {
        // 这里只是示例实现，需要根据实际订单对象的结构来获取退款状态
        try {
            // 使用反射获取refundStatus字段
            java.lang.reflect.Field field = order.getClass().getDeclaredField("refundStatus");
            field.setAccessible(true);
            Integer refundStatus = (Integer) field.get(order);
            
            // 根据退款状态值返回对应的描述
            switch (refundStatus) {
                case 0:
                    return "未退款";
                case 1:
                    return "退款中";
                case 2:
                    return "退款成功";
                case 3:
                    return "退款失败";
                default:
                    return "未知状态";
            }
        } catch (Exception e) {
            return "获取退款状态失败";
        }
    }
}
