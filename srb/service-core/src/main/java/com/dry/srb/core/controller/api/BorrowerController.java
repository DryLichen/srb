package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.pojo.vo.BorrowerVO;
import com.dry.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api(tags = "借款人接口")
@RestController
@RequestMapping("/api/core/borrower")
public class BorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    @ApiOperation("保存借款人信息")
    @RequestMapping(value = "/auth/save", method = RequestMethod.POST)
    public R saveBorrowerVOByUserId(@RequestBody BorrowerVO borrowerVO,
                                    HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowerService.saveBorrowerVOByUserId(borrowerVO, userId);
        return R.ok().message("借款人信息提交成功");
    }

    @ApiOperation("获取借款账号认证状态")
    @RequestMapping( "/auth/borrowerStatus")
    public R getBorrowerStatus(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        Integer status = borrowerService.getBorrowerStatusByUserId(userId);
        return R.ok().data("status", status);
    }
}

