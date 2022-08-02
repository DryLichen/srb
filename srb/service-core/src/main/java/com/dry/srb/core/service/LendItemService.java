package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.LendItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.InvestVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface LendItemService extends IService<LendItem> {

    /**
     * 生成投标请求到汇付宝的表单
     * 生成投标记录
     */
    String commitInvest(InvestVO investVO);

    /**
     * 投标回调
     */
    String notify(Map<String, Object> paramMap);

    /**
     * @param lendId
     * @return 获取投标记录列表
     */
    List<LendItem> getList(Long lendId);
}
