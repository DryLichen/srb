package com.dry.srb.core.controller.api;


import com.dry.common.exception.Assert;
import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import com.dry.common.util.RegexValidateUtils;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.pojo.vo.LoginVO;
import com.dry.srb.core.pojo.vo.RegisterVO;
import com.dry.srb.core.pojo.vo.UserIndexVO;
import com.dry.srb.core.pojo.vo.UserInfoVO;
import com.dry.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Slf4j
@Api(tags = "会员接口")
@RestController
@RequestMapping("/api/core/userInfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    // 这里不能使用redisTemplate的String泛型,因为redis里面存的不是一般String
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public R register(@RequestBody RegisterVO registerVO){
        String mobile = registerVO.getMobile();
        String code = registerVO.getCode();
        String password = registerVO.getPassword();

        // 先行对传入参数进行合法校验
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        // 判断验证码是否正确
        String redisCode = (String) redisTemplate.opsForValue().get("srb:sms:code:" + mobile);
        Assert.equals(redisCode, code, ResponseEnum.CODE_ERROR);

        // 注册
        userInfoService.register(registerVO);
        return R.ok().message("注册成功");
    }

    @ApiOperation("在发送验证码之前检查手机号是否已经注册")
    @RequestMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable("mobile") String mobile){
        return userInfoService.checkMobile(mobile);
    }

    @ApiOperation("会员登录")
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest httpServletRequest){
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();

        // 过滤不合法参数
        Assert.notNull(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notNull(password, ResponseEnum.PASSWORD_NULL_ERROR);

        // 获取登录ip
        String ip = httpServletRequest.getRemoteAddr();

        // 登录
        UserInfoVO userInfoVO = userInfoService.login(loginVO, ip);
        return R.ok().data("userInfoVO", userInfoVO);
    }

    @ApiOperation("检查用户是否拥有已登陆令牌")
    @RequestMapping("/checkToken")
    public R checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean flag = JwtUtils.checkToken(token);

        if(flag){
            return R.ok();
        } else {
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }
    }

    @ApiOperation("获取用户个人中心首页展示信息")
    @RequestMapping("/auth/getUserIndex")
    public R getUserIndex(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        UserIndexVO userIndexVO = userInfoService.getUserIndex(userId);
        return R.ok().data("userIndexVO", userIndexVO);
    }
}

