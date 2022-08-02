package com.dry.srb.core.controller.admin;

import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.LendItem;
import com.dry.srb.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "管理端投标记录接口")
@RestController
@RequestMapping("/admin/core/lendItem")
public class AdminLendItemController {

    @Autowired
    private LendItemService lendItemService;

    @ApiOperation("获取列表")
    @RequestMapping("/list/{lendId}")
    public R getList(@PathVariable("lendId") Long lendId){
        List<LendItem> lendItemList = lendItemService.getList(lendId);
        return R.ok().data("list", lendItemList);
    }
}
