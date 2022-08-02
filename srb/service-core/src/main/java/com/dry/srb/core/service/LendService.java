package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.dry.srb.core.pojo.entity.Lend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.BorrowInfoApprovalVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface LendService extends IService<Lend> {

    /**
     * 生成标的记录
     */
    void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo);

    /**
     * 获取标的列表
     * 查找关键词为用户Id或是借款申请的Id
     */
    List<Lend> getList(Long keyword);

    /**
     * 获取标的详情
     */
    Map<String, Object> getLendDetail(Long id);

    /**
     * 计算投资总收益
     */
    BigDecimal getYield(BigDecimal investAmount, BigDecimal yearRate, Integer totalMonth, Integer returnMethod);

    /**
     * 平台放款
     */
    void makeLoan(Long lendId);
}
