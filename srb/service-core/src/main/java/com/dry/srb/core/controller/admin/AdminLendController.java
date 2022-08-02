package com.dry.srb.core.controller.admin;

import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.service.LendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "标的管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/lend")
public class AdminLendController {

    @Autowired
    private LendService lendService;

    @ApiOperation("获取标的列表")
    @RequestMapping("/list")
    public R list(@RequestParam(required = false) Long keyword){
        List<Lend> list = lendService.getList(keyword);

        // 封装列表和总条目数
        HashMap<String, Object> map = new HashMap<>();
        int size = list.size();
        map.put("list", list);
        map.put("total", size);

        return R.ok().data(map);
    }

    @ApiOperation("获取标的详情")
    @RequestMapping("/show/{id}")
    public R show(@PathVariable("id") Long id){
        Map<String, Object> lendDetail = lendService.getLendDetail(id);
        return R.ok().data("lendDetail", lendDetail);
    }

    @ApiOperation("放款")
    @RequestMapping("/makeLoan/{lendId}")
    public R makeLoan(@PathVariable("lendId") Long lendId){
        lendService.makeLoan(lendId);
        return R.ok().message("放款成功");
    }
}
