package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.dry.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * <p>
 * 借款信息表 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "用户借款信息")
@Slf4j
@RestController
@RequestMapping("/api/core/borrowInfo")
public class BorrowInfoController {

    @Autowired
    private BorrowInfoService borrowInfoService;

    @ApiOperation("获取借款额度")
    @RequestMapping("/auth/getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        BigDecimal borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount", borrowAmount);
    }

    @ApiOperation("提交借款申请")
    @RequestMapping(value = "/auth/apply", method = RequestMethod.POST)
    public R apply(HttpServletRequest request,
                   @RequestBody BorrowInfo borrowInfo){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowInfoService.saveBorrowInfo(borrowInfo, userId);
        return R.ok().message("提交借款申请成功");
    }

    @ApiOperation("获取借款申请状态")
    @RequestMapping("/auth/getBorrowInfoStatus")
    public R getBorrowInfoStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer borrowInfoStatus = borrowInfoService.getStatusByUserId(userId);
        return R.ok().data("borrowInfoStatus", borrowInfoStatus);
    }
}

