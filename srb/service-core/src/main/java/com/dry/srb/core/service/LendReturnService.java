package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface LendReturnService extends IService<LendReturn> {

    /**
     * 生成还款计划：借款人还款
     * 标的每期还款都会生成一条记录
     */
    void repaymentLend(Lend lend);

    /**
     * @param lendId
     * @return 获取借款人还款计划列表
     */
    List<LendReturn> getList(Long lendId);

    /**
     * @return 生成还款请求到汇付宝的表单
     */
    String commitReturn(Long lendReturnId, Long userId);

    /**
     * 还款异步回调
     */
    String notifyUrl(Map<String, Object> paramMap);
}
