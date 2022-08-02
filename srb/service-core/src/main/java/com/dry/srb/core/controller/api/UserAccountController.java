package com.dry.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.service.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "会员账户管理")
@Slf4j
@RestController
@RequestMapping("/api/core/userAccount")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @ApiOperation("账户充值")
    @RequestMapping("/auth/commitCharge/{chargeAmt}")
    public R commitCharge(@PathVariable("chargeAmt") BigDecimal chargeAmt,
                          HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String formStr = userAccountService.commitCharge(chargeAmt, userId);
        return R.ok().data("formStr", formStr);
    }

    @ApiOperation("汇付宝充值异步回调")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String notify(HttpServletRequest request){
        // 1.转换Map值类型从String[]=>Object
        // 这一步是要满足验签方法的参数要求
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());

        // 2.检验签名
        if (RequestHelper.isSignEquals(paramMap)) {
            // 3.结果编码为0001时充值成功
            if ("0001".equals(paramMap.get("resultCode"))) {
                return userAccountService.notify(paramMap);
            } else {
                // 对于汇付宝，只要异步回调没有接收到success，就会尝试重试5次
                log.info("用户充值异步回调充值失败：" + JSON.toJSONString(paramMap));
                return "fail";
            }
        } else {
            log.info("用户充值异步回调签名错误：{}", JSON.toJSONString(paramMap));
            return "fail";
        }
    }

    @ApiOperation("获取用户账户余额")
    @RequestMapping("/auth/amount")
    public R getAmount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        BigDecimal amount = userAccountService.getAmount(userId);
        return R.ok().data("amount", amount);
    }

    @ApiOperation("用户提现")
    @RequestMapping("/auth/withdraw/{amount}")
    public R withdraw(@PathVariable("amount") BigDecimal amount, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        String form = userAccountService.withdraw(amount, userId);
        return R.ok().data("form", form);
    }

    @ApiOperation("汇付宝提现异步回调接口")
    @RequestMapping(value = "/notifyWithdraw", method = RequestMethod.POST)
    public String notifyWithdraw(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> paramMap = RequestHelper.switchMap(map);

        // 验签
        if(RequestHelper.isSignEquals(paramMap)){
            if("0001".equals(paramMap.get("resultCode"))) {
                return userAccountService.notifyWithdraw(paramMap);
            } else {
                log.error("用户提现异步回调提现失败：{}", JSON.toJSONString(paramMap));
            }
        } else {
            log.error("用户提现异步回调签名错误：{}", JSON.toJSONString(paramMap));
        }

        return "fail";
    }
}

