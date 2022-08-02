package com.dry.srb.core.controller.api;

import com.alibaba.fastjson.JSON;
import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.hfb.FormHelper;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.pojo.vo.UserBindVO;
import com.dry.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "会员账号绑定接口")
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Autowired
    private UserBindService userBindService;

    // 所有需要 token授权 的业务都加上 auth 路径
    @ApiOperation("提交账户绑定数据")
    @RequestMapping(value = "/auth/bind", method = RequestMethod.POST)
    public R bind(@ApiParam("前端传入的绑定表单数据") @RequestBody UserBindVO userBindVO,
                  HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String form = userBindService.commitBindUser(userBindVO, userId);
        return R.ok().data("form", form);
    }

    @ApiOperation("汇付宝异步回调接口")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String notify(HttpServletRequest request){
        Map<String, Object> map = RequestHelper.switchMap(request.getParameterMap());
        log.info("用户账号绑定异步回调：{}", JSON.toJSONString(map));

        //检验签名，确保是汇付宝发起的回调请求
        boolean result = RequestHelper.isSignEquals(map);
        if(result){
            userBindService.notify(map);
            return "success";
        }

        log.error("用户账号绑定异步回调签名错误：{}", JSON.toJSONString(map));
        return "fail";
    }
}
