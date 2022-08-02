package com.dry.srb.sms.controller.api;

import com.dry.common.exception.Assert;
import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import com.dry.common.util.RandomUtils;
import com.dry.common.util.RegexValidateUtils;
import com.dry.srb.sms.client.CoreUserInfoClient;
import com.dry.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = "短信服务管理")
// @CrossOrigin
@RestController
@RequestMapping("/api/sms")
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CoreUserInfoClient coreUserInfoClient;

    @ApiOperation("发送验证码短信")
    @RequestMapping(value = "/send/{mobile}", method = RequestMethod.GET)
    public R send(
            @ApiParam("电话号码")
            @PathVariable String mobile){
        // 1.判断请求的电话号码是否有效
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        //2.判断手机号是否被注册过
        boolean result = coreUserInfoClient.checkMobile(mobile);
        log.info("手机号注册检查result: {}", result);
        Assert.isTrue(!result, ResponseEnum.MOBILE_EXIST_ERROR);

        // 3.生成验证码；放入参数字符串数组，过期时间为5分钟
        String code = RandomUtils.getFourBitRandom();
        String[] params = new String[]{code, "5"};

        //************* 4.发送短信，测试时可以ban掉省钱，直接从redis中取出验证码 ***************
        // smsService.send(mobile, SmsProperties.TEMPLATE_ID, params);

        // 5.将验证码存入redis，5分钟内有效
        redisTemplate.opsForValue().set("srb:sms:code:" + mobile, code, 5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }
}
