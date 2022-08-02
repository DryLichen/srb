package com.dry.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.pojo.entity.LendItem;
import com.dry.srb.core.pojo.vo.InvestVO;
import com.dry.srb.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "标的投资")
@Slf4j
@RestController
@RequestMapping("/api/core/lendItem")
public class LendItemController {

    @Autowired
    private LendItemService lendItemService;

    @ApiOperation("会员提交投标申请")
    @RequestMapping(value = "/auth/commitInvest", method = RequestMethod.POST)
    public R commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        investVO.setInvestUserId(userId);
        investVO.setInvestName(userName);

        // 生成网页自动提交到汇付宝的表单
        String form = lendItemService.commitInvest(investVO);
        return R.ok().data("form", form);
    }

    @ApiOperation("用户投标异步回调")
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String notify(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());

        //校验签名
        if(RequestHelper.isSignEquals(paramMap)){
            if("0001".equals(paramMap.get("resultCode"))){
                lendItemService.notify(paramMap);
            } else {
                log.error("用户投标异步回调投标失败：{}", JSON.toJSONString(paramMap));
                return "fail";
            }
        } else {
            log.error("用户投标异步回调签名错误：{}", JSON.toJSONString(paramMap));
            return "fail";
        }

        return "success";
    }

    @ApiOperation("获取投标列表")
    @RequestMapping("/list/{lendId}")
    public R getList(@PathVariable("lendId") Long lendId){
        List<LendItem> list = lendItemService.getList(lendId);
        return R.ok().data("list", list);
    }
}

