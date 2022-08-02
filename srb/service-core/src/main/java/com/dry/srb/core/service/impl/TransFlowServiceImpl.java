package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.TransFlow;
import com.dry.srb.core.mapper.TransFlowMapper;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.service.TransFlowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {

    @Resource
    private TransFlowMapper transFlowMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 根据汇付宝回调参数生成交易流水
     */
    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        // 获取userInfo
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bind_code", transFlowBO.getBindCode());
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        // 生成交易流水对象
        TransFlow transFlow = new TransFlow();
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setTransType(transFlowBO.getTransTypeEnum().getTransType());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setMemo(transFlowBO.getMemo());

        transFlowMapper.insert(transFlow);
    }

    /**
     * @return 交易流水号是否存在
     */
    @Override
    public boolean TransNoExists(String transNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no", transNo).last("limit 1");
        Long count = transFlowMapper.selectCount(transFlowQueryWrapper);

        return (count > 0 ? true : false);
    }

    /**
     * @param userId
     * @return 根据用户id获取他的交易流水列表
     */
    @Override
    public List<TransFlow> getList(Long userId) {
        QueryWrapper<TransFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("id");
        List<TransFlow> transFlows = transFlowMapper.selectList(queryWrapper);
        return transFlows;
    }
}
