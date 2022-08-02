package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.pojo.entity.LendItemReturn;
import com.dry.srb.core.service.LendItemReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "前端回款计划接口")
@RestController
@RequestMapping("/api/core/lendItemReturn")
public class LendItemReturnController {

    @Autowired
    private LendItemReturnService lendItemReturnService;

    @ApiOperation("获取回款计划列表")
    @RequestMapping("/auth/list/{lendId}")
    public R getList(@PathVariable("lendId") Long lendId, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        List<LendItemReturn> list = lendItemReturnService.getList(lendId, userId);
        int size = list.size();
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("isInvestor", size > 0);

        return R.ok().data("result", result);
    }
}

