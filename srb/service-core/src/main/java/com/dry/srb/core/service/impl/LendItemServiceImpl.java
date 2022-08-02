package com.dry.srb.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.enums.LendStatusEnum;
import com.dry.srb.core.enums.TransTypeEnum;
import com.dry.srb.core.hfb.FormHelper;
import com.dry.srb.core.hfb.HfbConst;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.mapper.LendMapper;
import com.dry.srb.core.mapper.UserAccountMapper;
import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.pojo.entity.LendItem;
import com.dry.srb.core.mapper.LendItemMapper;
import com.dry.srb.core.pojo.vo.InvestVO;
import com.dry.srb.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.util.LendNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Slf4j
@Service
public class LendItemServiceImpl extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {

    @Autowired
    private LendItemMapper lendItemMapper;
    @Autowired
    private LendMapper lendMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private LendService lendService;
    @Autowired
    private UserBindService userBindService;
    @Autowired
    private TransFlowService transFlowService;

    /**
     * 生成自动提交表单
     * 生成投标记录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String commitInvest(InvestVO investVO) {
        // 获取标的及投标信息
        Long userId = investVO.getInvestUserId();
        String investName = investVO.getInvestName();
        Long lendId = investVO.getLendId();
        BigDecimal investAmount = new BigDecimal(investVO.getInvestAmount());

        Lend lend = lendMapper.selectById(lendId);

        // 检验：标的状态是否为募资中
        Integer status = lend.getStatus();
        Assert.isTrue(status == LendStatusEnum.INVEST_RUN.getStatus(), ResponseEnum.LEND_INVEST_ERROR);

        // 检验：标的是否超额募资
        BigDecimal sum = investAmount.add(lend.getInvestAmount());
        BigDecimal amount = lend.getAmount();
        Assert.isTrue(amount.compareTo(sum) >= 0, ResponseEnum.LEND_FULL_SCALE_ERROR);

        // 检验：投资人账户余额是否足够投资
        BigDecimal availableAmount = userAccountService.getAmount(userId);
        Assert.isTrue(availableAmount.compareTo(investAmount) >= 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 在尚融宝平台生成投标记录
        LendItem lendItem = new LendItem();
        lendItem.setLendItemNo(LendNoUtils.getLendItemNo());
        lendItem.setLendId(lendId);
        lendItem.setInvestUserId(userId);
        lendItem.setInvestName(investName);
        lendItem.setInvestAmount(investAmount);
        lendItem.setLendYearRate(lend.getLendYearRate());
        lendItem.setInvestTime(LocalDateTime.now());  //投资时间为提交投标申请时间
        lendItem.setLendStartDate(lend.getLendStartDate());
        lendItem.setLendEndDate(lend.getLendEndDate());

        BigDecimal expectAmount = lendService.getYield(investAmount,
                lend.getLendYearRate(), lend.getPeriod(), lend.getReturnMethod());
        lendItem.setExpectAmount(expectAmount); //预期收益

        lendItem.setRealAmount(new BigDecimal(0));
        lendItem.setStatus(0); //默认状态，刚刚创建

        lendItemMapper.insert(lendItem);

        // 生成表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);

        String voteBindCode = userBindService.getBindCodeByUserId(userId);
        String benefitBindCode = userBindService.getBindCodeByUserId(lend.getUserId());
        paramMap.put("voteBindCode", voteBindCode); //投资人绑定码
        paramMap.put("benefitBindCode", benefitBindCode); //借款人绑定码

        paramMap.put("agentProjectCode", lend.getLendNo()); //标的编号
        paramMap.put("agentProjectName", lend.getTitle()); //标的标题
        paramMap.put("agentBillNo", lendItem.getLendItemNo()); //投标编号（订单编号）
        paramMap.put("voteAmt", investAmount); //投标金额
        paramMap.put("votePrizeAmt", new BigDecimal(0));
        paramMap.put("voteFeeAmt", new BigDecimal(0));
        paramMap.put("projectAmt", lend.getAmount()); //标的总金额
        paramMap.put("note", null);
        paramMap.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());

        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign); //签名

        String form = FormHelper.buildForm(HfbConst.INVEST_URL, paramMap);
        return form;
    }

    /**
     * 投标回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        // 接口幂等性控制
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean result = transFlowService.TransNoExists(agentBillNo);
        if(result){
            log.warn("接口幂等性控制被调用");
            return "success";
        }

        // 更新用户账户余额
        String voteBindCode = (String) paramMap.get("voteBindCode");
        String voteAmt = (String) paramMap.get("voteAmt");
        userAccountMapper.updateAccountByBindCode(voteBindCode, new BigDecimal("-" + voteAmt), new BigDecimal(voteAmt));

        // 更新标的投资记录状态为已支付
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_item_no", agentBillNo);
        LendItem lendItem = lendItemMapper.selectOne(lendItemQueryWrapper);
        lendItem.setStatus(1);
        lendItemMapper.updateById(lendItem);

        // 更新标的 已投金额 投资人数
        Lend lend = lendMapper.selectById(lendItem.getLendId());
        lend.setInvestAmount(lend.getInvestAmount().add(lendItem.getInvestAmount()));
        lend.setInvestNum(lend.getInvestNum() + 1);
        lendMapper.updateById(lend);

        // 新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO();
        transFlowBO.setAgentBillNo(agentBillNo);
        transFlowBO.setBindCode(voteBindCode);
        transFlowBO.setTransTypeEnum(TransTypeEnum.INVEST_LOCK);
        transFlowBO.setAmount(new BigDecimal(voteAmt));
        transFlowBO.setMemo("投资项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
        transFlowService.saveTransFlow(transFlowBO);

        log.info("用户投资异步回调成功：{}", JSON.toJSONString(paramMap));
        return "success";
    }

    /**
     * @param lendId
     * @return 获取投标记录列表
     */
    @Override
    public List<LendItem> getList(Long lendId) {
        QueryWrapper<LendItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lend_id", lendId);
        List<LendItem> lendItems = lendItemMapper.selectList(queryWrapper);
        return lendItems;
    }

}
