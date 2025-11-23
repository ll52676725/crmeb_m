package com.zbkj.search.enums;

/**
 * 意图类型枚举
 * @author CRMEB
 * @since 2024-05-20
 */
public enum IntentTypeEnum {

    /** 查询订单状态 */
    QUERY_ORDER_STATUS("query_order_status", "查询订单状态"),
    
    /** 查询物流信息 */
    QUERY_LOGISTICS("query_logistics", "查询物流信息"),
    
    /** 查询退款状态 */
    QUERY_REFUND_STATUS("query_refund_status", "查询退款状态"),
    
    /** 查询待支付订单 */
    QUERY_UNPAID_ORDERS("query_unpaid_orders", "查询待支付订单"),
    
    /** 查询待发货订单 */
    QUERY_UNSHIPPED_ORDERS("query_unshipped_orders", "查询待发货订单"),
    
    /** 查询待收货订单 */
    QUERY_UNRECEIVED_ORDERS("query_unreceived_orders", "查询待收货订单"),
    
    /** 查询已完成订单 */
    QUERY_COMPLETED_ORDERS("query_completed_orders", "查询已完成订单"),
    
    /** 查询已取消订单 */
    QUERY_CANCELLED_ORDERS("query_cancelled_orders", "查询已取消订单"),
    
    /** 其他意图 */
    OTHER("other", "其他意图");

    private final String code;
    private final String description;

    IntentTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取枚举
     * @param code 代码
     * @return 枚举
     */
    public static IntentTypeEnum getByCode(String code) {
        for (IntentTypeEnum intentType : values()) {
            if (intentType.code.equals(code)) {
                return intentType;
            }
        }
        return OTHER;
    }
}
