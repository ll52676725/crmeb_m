package com.zbkj.admin.controller;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.common.request.QuarterReportRequest;
import com.zbkj.common.response.CommonResult;
import com.zbkj.service.service.QuarterReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("api/admin/report/quarter")
@Api(tags = "报表 -- 季度报表")
public class QuarterReportController {

    @Autowired
    private QuarterReportService quarterReportService;

    @PreAuthorize("hasAuthority('admin:report:quarter:export')")
    @ApiOperation(value = "导出季度经营报表PDF")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportQuarterReport(@Validated QuarterReportRequest request) {
        String fileName = quarterReportService.exportQuarterReport(request);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }
}
