package com.dry.srb.core.service;

import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface TransFlowService extends IService<TransFlow> {

    /**
     * 根据汇付宝回调参数生成交易流水
     */
    void saveTransFlow(TransFlowBO transFlowBO);

    /**
     * 判断交易流水号是否已存在
     */
    boolean TransNoExists(String transNo);

    /**
     * @param userId
     * @return 根据用户id获取他的交易流水列表
     */
    List<TransFlow> getList(Long userId);
}
