package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.pojo.entity.LendItemReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface LendItemReturnService extends IService<LendItemReturn> {

    /**
     * 生成回款计划：将还款打给投资人
     * @param lendReturnMap 还款期数：还款计划id（因为每一期还款都会生成一个还款计划id，即LendReturnId）
     */
    List<LendItemReturn> returnLendItem(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend);

    /**
     * @return 获取投资人投资的某标的的回款计划
     */
    List<LendItemReturn> getList(Long lendId, Long userId);

    /**
     * 获取添加到还款请求中的回款明细数据
     */
    List<Map<String, Object>> addReturnDetail(Long lendReturnId);
}
