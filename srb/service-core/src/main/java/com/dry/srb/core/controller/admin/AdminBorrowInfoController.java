package com.dry.srb.core.controller.admin;

import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.dry.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.dry.srb.core.service.BorrowInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "管理借款申请")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Autowired
    private BorrowInfoService borrowInfoService;

    //关键词为用户名称或电话号码
    @ApiOperation("获取借款申请分页列表")
    @RequestMapping("/list/{page}/{size}")
    public R getListPage(@PathVariable Long page,
                      @PathVariable Long size,
                      @RequestParam(required = false) String keyword){
        List<BorrowInfo> listPage = borrowInfoService.getListPage(page, size, keyword);
        Long total = borrowInfoService.getTotal(keyword);

        HashMap<String, Object> map = new HashMap<>();
        map.put("listPage", listPage);
        map.put("total", total);

        return R.ok().data(map);
    }

    @ApiOperation("获取借款申请详情")
    @RequestMapping("/show/{id}")
    public R show(@PathVariable("id") Long id){
        Map<String, Object> borrowInfoDetail = borrowInfoService.getBorrowInfoDetail(id);
        return R.ok().data("borrowInfoDetail", borrowInfoDetail);
    }

    @ApiOperation("审批借款申请")
    @RequestMapping(value = "/approval", method = RequestMethod.POST)
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){
        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("借款申请审批完成");
    }
}
