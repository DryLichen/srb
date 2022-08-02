package com.dry.srb.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.enums.LendStatusEnum;
import com.dry.srb.core.enums.TransTypeEnum;
import com.dry.srb.core.hfb.FormHelper;
import com.dry.srb.core.hfb.HfbConst;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.mapper.*;
import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.pojo.entity.LendItem;
import com.dry.srb.core.pojo.entity.LendItemReturn;
import com.dry.srb.core.pojo.entity.LendReturn;
import com.dry.srb.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.util.LendNoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 还款记录表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class LendReturnServiceImpl extends ServiceImpl<LendReturnMapper, LendReturn> implements LendReturnService {

    @Autowired
    private LendItemMapper lendItemMapper;
    @Autowired
    private LendReturnMapper lendReturnMapper;
    @Autowired
    private LendMapper lendMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private LendItemReturnMapper lendItemReturnMapper;

    @Autowired
    private LendItemReturnService lendItemReturnService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private UserBindService userBindService;
    @Autowired
    private TransFlowService transFlowService;

    /**
     * 生成还款计划：借款人还款
     * 标的每期还款都会生成一条记录
     */
    public void repaymentLend(Lend lend){
        // 1.根据总期数创建还款计划列表
        List<LendReturn> lendReturnList = new ArrayList<>();

        for (int i = 1;i <= lend.getPeriod();i++){
            LendReturn lendReturn = new LendReturn();

            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setReturnNo(LendNoUtils.getReturnNo()); //还款流水号
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount()); //计划借的金额
            lendReturn.setBaseAmount(lend.getInvestAmount()); //实际借到的金额
            lendReturn.setCurrentPeriod(i);
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setReturnMethod(lend.getReturnMethod());
            lendReturn.setFee(new BigDecimal(0));
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i));
            lendReturn.setOverdue(false);
            lendReturn.setLast(lend.getPeriod() == i);
            lendReturn.setStatus(0);

            lendReturnList.add(lendReturn);
        }

        // 2.批量插入数据库，注意此时本金利息总额还未设置
        saveBatch(lendReturnList);

        // 3.生成还款期数与还款id的一一对应的map
        // 卧槽真的有病，直接传还款id不就行了。。。。。。。。。。。
        Map<Integer, Long> lendReturnMap = lendReturnList.stream()
                .collect(Collectors.toMap(LendReturn::getCurrentPeriod, LendReturn::getId));

        // 4.获取该标的对应的所有回款计划列表
        List<LendItemReturn> allLendItemReturnList = new ArrayList<>();
        List<LendItem> lendItemList = this.getLendItemListByLendId(lend.getId(), 1);
        lendItemList.forEach(lendItem -> {
            List<LendItemReturn> lendItemReturnList = lendItemReturnService.returnLendItem(lendItem.getId(), lendReturnMap, lend);
            allLendItemReturnList.addAll(lendItemReturnList);
        });

        // 5.设置本金利息总额
        for( LendReturn lendReturn : lendReturnList){
            //每月本金
            BigDecimal monthPrincipal = allLendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId() == lendReturn.getId())
                    .map(LendItemReturn::getPrincipal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lendReturn.setPrincipal(monthPrincipal);

            //每月利息
            BigDecimal monthInterest = allLendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId() == lendReturn.getId())
                    .map(LendItemReturn::getInterest)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lendReturn.setInterest(monthInterest);

            //每月本息
            BigDecimal monthTotal = allLendItemReturnList.stream()
                    .filter(item -> item.getLendReturnId() == lendReturn.getId())
                    .map(LendItemReturn::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            lendReturn.setTotal(monthTotal);
        }
        updateBatchById(lendReturnList);
    }

    /**
     * @param lendId
     * @return 获取借款人还款计划列表
     */
    @Override
    public List<LendReturn> getList(Long lendId) {
        QueryWrapper<LendReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lend_id", lendId);
        List<LendReturn> lendReturnList = lendReturnMapper.selectList(queryWrapper);
        return lendReturnList;
    }

    /**
     * @return 生成还款请求到汇付宝的表单
     */
    @Override
    public String commitReturn(Long lendReturnId, Long userId) {
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);

        // 1.判断用户账户余额是否足够还款
        BigDecimal balance = userAccountService.getAmount(userId);
        BigDecimal total = lendReturn.getTotal();
        Assert.isTrue(balance.compareTo(total) >= 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 2.获取表单所需参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);

        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        String title = lend.getTitle();
        paramMap.put("agentGoodsName", title);

        List<Map<String, Object>> data = lendItemReturnService.addReturnDetail(lendReturnId);
        paramMap.put("data", JSON.toJSONString(data));

        paramMap.put("agentBatchNo", lendReturn.getReturnNo());
        paramMap.put("fromBindCode", userBindService.getBindCodeByUserId(userId));
        paramMap.put("totalAmt", total);
        paramMap.put("note", null);
        paramMap.put("voteFeeAmt", new BigDecimal(0));
        paramMap.put("returnUrl", HfbConst.BORROW_RETURN_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.BORROW_RETURN_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        String form = FormHelper.buildForm(HfbConst.BORROW_RETURN_URL, paramMap);
        return form;
    }

    /**
     * 还款异步回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notifyUrl(Map<String, Object> paramMap) {
        //接口幂等性控制
        String agentBatchNo = (String) paramMap.get("agentBatchNo");
        if(transFlowService.TransNoExists(agentBatchNo)){
            log.warn("接口幂等性控制被调用!");
            return "success";
        }

        //更新还款计划
        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("return_no", agentBatchNo);
        LendReturn lendReturn = lendReturnMapper.selectOne(lendReturnQueryWrapper);
        lendReturn.setFee(new BigDecimal((String) paramMap.get("voteFeeAmt")));
        lendReturn.setStatus(1);
        lendReturn.setRealReturnTime(LocalDateTime.now());
        lendReturnMapper.updateById(lendReturn);

        //更新标的状态
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        if(lendReturn.getLast()){
            lend.setStatus(LendStatusEnum.PAY_OK.getStatus());
            lendMapper.updateById(lend);
        }

        //更新借款人账户
        String totalAmt = (String) paramMap.get("totalAmt");
        String bindCode = userBindService.getBindCodeByUserId(lendReturn.getUserId());
        userAccountMapper.updateAccountByBindCode(bindCode, new BigDecimal("-" + totalAmt), new BigDecimal(0));

        //添加借款人资金转出流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBatchNo,
                bindCode,
                new BigDecimal(totalAmt),
                TransTypeEnum.RETURN_DOWN,
                "借款人还款扣减，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle()
        );
        transFlowService.saveTransFlow(transFlowBO);

        //更新投资人账户
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("lend_return_id", lendReturn.getId());
        List<LendItemReturn> lendItemReturnList = lendItemReturnMapper.selectList(lendItemReturnQueryWrapper);
        lendItemReturnList.forEach( item -> {
            //更新回款状态
            item.setStatus(1);
            item.setRealReturnTime(LocalDateTime.now());
            lendItemReturnMapper.updateById(item);
            //更新出借信息
            LendItem lendItem = lendItemMapper.selectById(item.getLendItemId());
            lendItem.setRealAmount(item.getInterest());
            lendItemMapper.updateById(lendItem);
            //投资账号转入金额
            String investBindCode = userBindService.getBindCodeByUserId(item.getInvestUserId());
            userAccountMapper.updateAccountByBindCode(investBindCode, item.getTotal(), new BigDecimal(0));
            //投资账号交易流水
            TransFlowBO investTransFlowBO = new TransFlowBO(
                    LendNoUtils.getReturnItemNo(),
                    investBindCode,
                    item.getTotal(),
                    TransTypeEnum.INVEST_BACK,
                    "还款到账，项目编号：" + lend.getLendNo() + "，项目名称：" + lend.getTitle());
            transFlowService.saveTransFlow(investTransFlowBO);
        });

        return "success";
    }

    /**
     * 根据借款项目 lendId 查询特定状态的投标记录
     */
    private List<LendItem> getLendItemListByLendId(Long lendId, Integer status) {
        QueryWrapper<LendItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lend_id", lendId).eq("status", status);
        List<LendItem> lendItems = lendItemMapper.selectList(queryWrapper);

        return lendItems;
    }
}
