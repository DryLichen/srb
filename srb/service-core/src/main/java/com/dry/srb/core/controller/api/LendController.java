package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "标的")
@RestController
@RequestMapping("/api/core/lend")
public class LendController {

    @Autowired
    private LendService lendService;

    @ApiOperation("获取标的列表")
    @RequestMapping("/list")
    public R getList(@RequestParam(required = false) Long keyword){
        List<Lend> lendList = lendService.getList(keyword);
        return R.ok().data("lendList", lendList);
    }

    @ApiOperation("获取标的详情")
    @RequestMapping("/show/{id}")
    public R show(@PathVariable("id") Long id){
        Map<String, Object> lendDetail = lendService.getLendDetail(id);
        return R.ok().data("lendDetail", lendDetail);
    }

    @ApiOperation("获取投资收益")
    @RequestMapping("/getYield/{investAmount}/{yearRate}/{totalMonth}/{returnMethod}")
    public R getYield(@PathVariable("investAmount") BigDecimal investAmount,
                      @PathVariable("yearRate") BigDecimal yearRate,
                      @PathVariable("totalMonth") Integer totalMonth,
                      @PathVariable("returnMethod") Integer returnMethod){
        BigDecimal yield = lendService.getYield(investAmount, yearRate, totalMonth, returnMethod);
        return R.ok().data("yield", yield);
    }
}

