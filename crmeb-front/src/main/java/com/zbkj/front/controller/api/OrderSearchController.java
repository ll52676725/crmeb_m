package com.zbkj.front.controller.api;

import com.zbkj.common.model.order.StoreOrderEs;
import com.zbkj.common.response.CommonResult;
import com.zbkj.service.service.order.StoreOrderEsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * 订单搜索API控制器
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
@RestController
@RequestMapping("/api/order/search")
@Api(tags = "订单搜索")
public class OrderSearchController {

    @Autowired
    private StoreOrderEsService storeOrderEsService;

    @ApiOperation(value = "根据ID获取订单")
    @GetMapping("/get")
    public CommonResult<StoreOrderEs> getOrderById(
            @ApiParam(value = "订单ID", required = true) @RequestParam Integer id) {
        Optional<StoreOrderEs> orderOptional = storeOrderEsService.getOrderFromEs(id);
        return orderOptional.map(CommonResult::success).orElse(CommonResult.failed("订单不存在"));
    }

    @ApiOperation(value = "根据用户ID获取订单列表")
    @GetMapping("/byUid")
    public CommonResult<List<StoreOrderEs>> getOrdersByUid(
            @ApiParam(value = "用户ID", required = true) @RequestParam Integer uid) {
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByUidFromEs(uid);
        return CommonResult.success(orders);
    }

    @ApiOperation(value = "根据订单状态获取订单列表")
    @GetMapping("/byStatus")
    public CommonResult<List<StoreOrderEs>> getOrdersByStatus(
            @ApiParam(value = "订单状态", required = true) @RequestParam Integer status) {
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByStatusFromEs(status);
        return CommonResult.success(orders);
    }

    @ApiOperation(value = "根据用户ID和订单状态获取订单列表")
    @GetMapping("/byUidAndStatus")
    public CommonResult<List<StoreOrderEs>> getOrdersByUidAndStatus(
            @ApiParam(value = "用户ID", required = true) @RequestParam Integer uid,
            @ApiParam(value = "订单状态", required = true) @RequestParam Integer status) {
        List<StoreOrderEs> orders = storeOrderEsService.findOrdersByUidAndStatusFromEs(uid, status);
        return CommonResult.success(orders);
    }

    @ApiOperation(value = "根据订单号获取订单")
    @GetMapping("/byOrderId")
    public CommonResult<StoreOrderEs> getOrderByOrderId(
            @ApiParam(value = "订单号", required = true) @RequestParam String orderId) {
        StoreOrderEs order = storeOrderEsService.findOrderByOrderIdFromEs(orderId);
        return order != null ? CommonResult.success(order) : CommonResult.failed("订单不存在");
    }

    @ApiOperation(value = "关键词搜索订单")
    @GetMapping("/keyword")
    public CommonResult<Page<StoreOrderEs>> searchOrdersByKeyword(
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam Integer page,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam Integer limit) {
        // 构建分页参数
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<StoreOrderEs> orders = storeOrderEsService.searchOrdersByKeyword(keyword, pageable);
        return CommonResult.success(orders);
    }

    @ApiOperation(value = "重建订单索引")
    @GetMapping("/rebuildIndex")
    public CommonResult<String> rebuildOrderIndex() {
        storeOrderEsService.rebuildEsIndex();
        return CommonResult.success("订单索引重建成功");
    }

    @ApiOperation(value = "初始化订单索引")
    @GetMapping("/initIndex")
    public CommonResult<String> initOrderIndex() {
        storeOrderEsService.initEsIndex();
        return CommonResult.success("订单索引初始化成功");
    }
}
