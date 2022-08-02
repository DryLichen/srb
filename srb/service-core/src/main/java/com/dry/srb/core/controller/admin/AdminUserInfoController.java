package com.dry.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.pojo.query.UserInfoQuery;
import com.dry.srb.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "会员管理")
@Slf4j
// @CrossOrigin
@RestController
@RequestMapping("/admin/core/userInfo")
public class AdminUserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation("获取会员分页列表")
    @RequestMapping("/list/{page}/{limit}")
    public R listPage(@ApiParam("页码") @PathVariable("page") Long page,
                      @ApiParam("每页展示数量") @PathVariable("limit") Long limit,
                      @ApiParam("筛选条件") UserInfoQuery userInfoQuery){
        Page<UserInfo> pageParam = new Page<>(page, limit);
        IPage<UserInfo> pageModel = userInfoService.listPage(pageParam, userInfoQuery);
        return R.ok().data("pageModel", pageModel);
    }

    @RequestMapping("/lock/{id}/{status}")
    public R lock(@ApiParam("用户id") @PathVariable("id") Long id,
                  @ApiParam("锁定状态：1解锁，0锁定") @PathVariable("status") Integer status){
        userInfoService.lock(id, status);
        return R.ok().message(status == 1?"解锁成功":"锁定成功");
    }
}
