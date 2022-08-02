package com.dry.srb.core.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.Borrower;
import com.dry.srb.core.pojo.vo.BorrowerApprovalVO;
import com.dry.srb.core.pojo.vo.BorrowerDetailVO;
import com.dry.srb.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "借款人审核接口")
@Slf4j
@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Autowired
    private BorrowerService borrowerService;

    @ApiOperation("获取分页的借款人列表")
    @RequestMapping("/list/{page}/{limit}")
    public R listPage(@PathVariable("page") Long page,
                      @PathVariable("limit") Long limit,
                      @RequestParam(required = false) String keyword){
        Page<Borrower> pageParam = new Page<>(page, limit);
        IPage<Borrower> pageModel = borrowerService.listPage(pageParam, keyword);
        return R.ok().data("pageModel", pageModel);
    }

    @ApiOperation("根据借款人id获取借款人详细信息")
    @RequestMapping("/show/{id}")
    public R getBorrowerDetailVOById(@PathVariable Long id){
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(id);
        return R.ok().data("borrowDetailVO", borrowerDetailVO);
    }

    @ApiOperation("借款额度审批")
    @RequestMapping(value = "/approval", method = RequestMethod.POST)
    public R approval(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
        borrowerService.approval(borrowerApprovalVO);
        return R.ok().message("借款人信息审核完成");
    }

}
