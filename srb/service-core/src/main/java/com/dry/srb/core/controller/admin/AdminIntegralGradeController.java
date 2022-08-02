package com.dry.srb.core.controller.admin;

import com.dry.common.exception.Assert;
import com.dry.common.exception.BusinessException;
import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.pojo.entity.IntegralGrade;
import com.dry.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@RestController
// @CrossOrigin
@RequestMapping("/admin/core/integralGrade")
@Api(tags = "积分等级管理")
public class AdminIntegralGradeController {

    @Autowired
    private IntegralGradeService service;

    @ApiOperation("获取积分等级列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public R listAll(){
        //测试日志框架logback
        // log.info("log information!...");
        // log.warn("It's a warning!");
        // log.error("It's an error!");

        List<IntegralGrade> list = service.list();
        if(list != null){
            return R.ok().data("list", list);
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation(value = "根据id删除积分等级", notes = "逻辑删除")
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public R removeById(
            @ApiParam(value = "数据id", example = "1", required = true)
            @PathVariable("id") Long id){
        Boolean result = service.removeById(id);
        if(result){
            return R.ok().message("删除成功");
        }
        return R.error().message("删除失败");
    }

    @ApiOperation("新增积分等级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade){
        // 借款额度为空时抛出异常
        // if(integralGrade.getBorrowAmount() == null){
        //     throw new BusinessException(ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        // }
        // 方法二：利用断言控制参数值
        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);

        Boolean result = service.save(integralGrade);
        if(result){
            return R.ok().message("保存成功");
        }
        return R.error().message("保存失败");
    }

    @ApiOperation("根据id获取积分等级")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public R getById(
            @ApiParam(value = "积分等级对象", required = true)
            @PathVariable("id") Long id){
        IntegralGrade integralGrade = service.getById(id);
        if(integralGrade != null){
            return R.ok().data("record", integralGrade);
        }
        return R.error().message("数据不存在");
    }

    @ApiOperation("根据id更新积分等级对象")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public R updateById(
            @ApiParam(value = "积分等级对象", required = true)
            @RequestBody IntegralGrade integralGrade){
        Boolean result = service.updateById(integralGrade);
        if(result){
            return R.ok().message("更新成功");
        }
        return R.error().message("更新失败");
    }
}
