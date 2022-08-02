package com.dry.srb.core.controller.admin;

import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.UserLoginRecord;
import com.dry.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "会员登录日志接口")
@Slf4j
// @CrossOrigin
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Autowired
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("列举出某会员最近50次登录记录")
    @RequestMapping("listTop50/{userId}")
    public R listTop50(@ApiParam("会员id") @PathVariable("userId") Long userId){
        List<UserLoginRecord> userLoginRecordList = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", userLoginRecordList);
    }
}
