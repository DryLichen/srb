package com.dry.srb.core.controller.admin;

import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.LendReturn;
import com.dry.srb.core.service.LendReturnService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "管理借款人还款")
@RestController
@RequestMapping("/admin/core/lendReturn")
public class AdminLendReturnController {

    @Autowired
    private LendReturnService lendReturnService;

    @ApiOperation("获取借款人还款列表")
    @RequestMapping("/list/{lendId}")
    public R getList(@PathVariable("lendId") Long lendId){
        List<LendReturn> list = lendReturnService.getList(lendId);
        return R.ok().data("list", list);
    }
}
