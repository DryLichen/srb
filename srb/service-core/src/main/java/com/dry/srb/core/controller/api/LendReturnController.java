package com.dry.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.pojo.entity.LendReturn;
import com.dry.srb.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "客户端借款人还款接口")
@Slf4j
@RestController
@RequestMapping("/api/core/lendReturn")
public class LendReturnController {

    @Autowired
    private LendReturnService lendReturnService;

    @ApiOperation("获取还款计划列表")
    @RequestMapping("/list/{lendId}")
    public R getList(@PathVariable("lendId") Long lendId){
        List<LendReturn> list = lendReturnService.getList(lendId);
        return R.ok().data("list", list);
    }

    @ApiOperation("借款人还款")
    @RequestMapping("/auth/commitReturn/{lendReturnId}")
    public R commitReturn(@PathVariable("lendReturnId") Long lendReturnId,
                          HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String form = lendReturnService.commitReturn(lendReturnId, userId);
        return R.ok().data("form", form);
    }

    @ApiOperation("还款回调接口")
    @RequestMapping("/notifyUrl")
    public String notifyUrl(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());

        if (RequestHelper.isSignEquals(paramMap)) {
            if ("0001".equals(paramMap.get("resultCode"))) {
                return lendReturnService.notifyUrl(paramMap);
            } else {
                log.error("还款异步回调还款失败：{}", JSON.toJSONString(paramMap));
            }
        } else {
            log.error("还款异步回调签名错误：{}", JSON.toJSONString(paramMap));
        }

        return "fail";
    }
}

