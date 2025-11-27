package com.zbkj.common.model.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单Elasticsearch文档类
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
@Data
@Document(indexName = "store_order", type = "_doc", shards = 5, replicas = 1)
public class StoreOrderEs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String orderId;

    private Integer uid;

    private String realName;

    private String userPhone;

    private String userAddress;

    private BigDecimal freightPrice;

    private Integer totalNum;

    private BigDecimal totalPrice;

    private BigDecimal totalPostage;

    private BigDecimal payPrice;

    private BigDecimal payPostage;

    private BigDecimal deductionPrice;

    private Integer couponId;

    private BigDecimal couponPrice;

    private Boolean paid;

    private Date payTime;

    private String payType;

    private Date createTime;

    private Integer status;

    private Integer refundStatus;

    private String refundReasonWapImg;

    private String refundReasonWapExplain;

    private String refundReasonWap;

    private String refundReason;

    private Date refundReasonTime;

    private BigDecimal refundPrice;

    private String deliveryName;

    private String deliveryType;

    private String deliveryId;

    private Integer gainIntegral;

    private Integer useIntegral;

    private Integer backIntegral;

    private String mark;

    private Boolean isDel;

    private String remark;

    private Integer merId;

    private Integer isMerCheck;

    private Integer combinationId;

    private Integer pinkId;

    private BigDecimal cost;

    private Integer seckillId;

    private Integer bargainId;

    private Integer bargainUserId;

    private String verifyCode;

    private Integer storeId;

    private Integer shippingType;

    private Integer clerkId;

    private Integer isChannel;

    private Boolean isRemind;

    private Boolean isSystemDel;

    private Date updateTime;

    private String deliveryCode;

    private Integer type;

    // getter和setter方法
}
