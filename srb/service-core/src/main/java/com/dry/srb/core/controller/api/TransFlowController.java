package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.pojo.entity.TransFlow;
import com.dry.srb.core.service.TransFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 交易流水表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "资金记录")
@Slf4j
@RestController
@RequestMapping("/api/core/transFlow")
public class TransFlowController {

    @Autowired
    private TransFlowService transFlowService;

    @ApiOperation("获取用户资金流水列表")
    @RequestMapping("/auth/list")
    public R getList(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        List<TransFlow> transFlowList = transFlowService.getList(userId);
        return R.ok().data("list", transFlowList);
    }
}

