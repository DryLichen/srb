package com.dry.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    /**
     * 获取用户的 借款额度
     */
    BigDecimal getBorrowAmount(Long userId);

    /**
     * 客户端 用户提交 借款申请
     */
    void saveBorrowInfo(BorrowInfo borrowInfo, Long userId);

    /**
     * 获取用户借款申请 审核状态
     */
    Integer getStatusByUserId(Long userId);

    /**
     * 获取借款申请的 分页列表数据
     */
    List<BorrowInfo> getListPage(Long page, Long size, String keyword);

    /**
     * 获取借款申请的 总条目数
     */
    Long getTotal(String keyword);

    /**
     * 获取借款申请详情
     */
    Map<String, Object> getBorrowInfoDetail(Long id);

    /**
     * 借款申请审批
     */
    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
