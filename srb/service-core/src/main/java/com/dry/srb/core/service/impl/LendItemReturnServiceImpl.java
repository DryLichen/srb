package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.srb.core.enums.ReturnMethodEnum;
import com.dry.srb.core.mapper.LendItemMapper;
import com.dry.srb.core.mapper.LendMapper;
import com.dry.srb.core.mapper.LendReturnMapper;
import com.dry.srb.core.pojo.entity.Lend;
import com.dry.srb.core.pojo.entity.LendItem;
import com.dry.srb.core.pojo.entity.LendItemReturn;
import com.dry.srb.core.mapper.LendItemReturnMapper;
import com.dry.srb.core.pojo.entity.LendReturn;
import com.dry.srb.core.service.LendItemReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.service.UserBindService;
import com.dry.srb.core.util.Amount1Helper;
import com.dry.srb.core.util.Amount2Helper;
import com.dry.srb.core.util.Amount3Helper;
import com.dry.srb.core.util.Amount4Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Autowired
    private LendItemMapper lendItemMapper;
    @Autowired
    private LendItemReturnMapper lendItemReturnMapper;
    @Autowired
    private LendReturnMapper lendReturnMapper;
    @Autowired
    private LendMapper lendMapper;

    @Autowired
    private UserBindService userBindService;

    /**
     * 生成回款计划：将还款打给投资人
     * @param lendReturnMap 还款期数：还款计划id（因为每一期还款都会生成一个还款计划id，即LendReturnId）
     */
    @Override
    public List<LendItemReturn> returnLendItem(Long lendItemId, Map<Integer, Long> lendReturnMap, Lend lend){
        // 1.获取投标对象
        LendItem lendItem = lendItemMapper.selectById(lendItemId);

        // 2.获取期数:回款本金和利息的map
        BigDecimal investAmount = lendItem.getInvestAmount();
        BigDecimal lendYearRate = lendItem.getLendYearRate();
        Integer period = lend.getPeriod();

        Map<Integer, BigDecimal> perMonthPrincipal = null;
        Map<Integer, BigDecimal> perMonthInterest = null;

        Integer returnMethod = lend.getReturnMethod();
        if(returnMethod == ReturnMethodEnum.ONE.getMethod()){
            perMonthPrincipal = Amount1Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
            perMonthInterest = Amount1Helper.getPerMonthInterest(investAmount, lendYearRate, period);
        } else if (returnMethod == ReturnMethodEnum.TWO.getMethod()){
            perMonthPrincipal = Amount2Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
            perMonthInterest = Amount2Helper.getPerMonthInterest(investAmount, lendYearRate, period);
        } else if (returnMethod == ReturnMethodEnum.THREE.getMethod()){
            perMonthPrincipal = Amount3Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
            perMonthInterest = Amount3Helper.getPerMonthInterest(investAmount, lendYearRate, period);
        } else {
            perMonthPrincipal = Amount4Helper.getPerMonthPrincipal(investAmount, lendYearRate, period);
            perMonthInterest = Amount4Helper.getPerMonthInterest(investAmount, lendYearRate, period);
        }

        // 3.生成回款计划列表
        List<LendItemReturn> lendItemReturnList = new ArrayList<>();
        for(Integer currentPeriod : lendReturnMap.keySet()) {
            LendItemReturn lendItemReturn = new LendItemReturn();

            lendItemReturn.setLendReturnId(lendReturnMap.get(currentPeriod));
            lendItemReturn.setLendItemId(lendItemId);
            lendItemReturn.setLendId(lend.getId());
            lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
            lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
            lendItemReturn.setCurrentPeriod(currentPeriod);
            lendItemReturn.setLendYearRate(lend.getLendYearRate());
            lendItemReturn.setReturnMethod(lend.getReturnMethod());

            // 最后一次回款计算：总金额减去之前的金额，以消除前面的计算误差
            if(currentPeriod == lend.getPeriod()) {
                //本金
                BigDecimal sumPrincipal = lendItemReturnList.stream()
                        .map(LendItemReturn::getPrincipal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastPrincipal = lendItem.getInvestAmount().subtract(sumPrincipal);
                lendItemReturn.setPrincipal(lastPrincipal);

                //利息
                BigDecimal sumInterest = lendItemReturnList.stream()
                        .map(LendItemReturn::getInterest)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal lastInterest = lendItem.getExpectAmount().subtract(sumInterest);
                lendItemReturn.setInterest(lastInterest);
            } else {
                lendItemReturn.setPrincipal(perMonthPrincipal.get(currentPeriod));
                lendItemReturn.setInterest(perMonthInterest.get(currentPeriod));
            }

            lendItemReturn.setTotal(lendItemReturn.getPrincipal().add(lendItemReturn.getInterest()));
            lendItemReturn.setFee(new BigDecimal(0));
            lendItemReturn.setReturnDate(lend.getLendStartDate().plusMonths(currentPeriod));
            lendItemReturn.setOverdue(false);
            lendItemReturn.setStatus(0);

            lendItemReturnList.add(lendItemReturn);
        }

        saveBatch(lendItemReturnList);
        return lendItemReturnList;
    }

    /**
     * @return 获取投资人投资的某标的的回款计划
     */
    @Override
    public List<LendItemReturn> getList(Long lendId, Long userId) {
        QueryWrapper<LendItemReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("invest_user_id", userId)
                .eq("lend_id", lendId)
                .orderByAsc("current_period");
        List<LendItemReturn> lendItemReturnList = lendItemReturnMapper.selectList(queryWrapper);
        return lendItemReturnList;
    }

    /**
     * 获取添加到还款请求中的回款明细数据
     */
    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {
        // 获取还款计划的回款计划列表
        QueryWrapper<LendItemReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("lend_return_id", lendReturnId);
        List<LendItemReturn> lendItemReturnList = lendItemReturnMapper.selectList(queryWrapper);

        //获取lendNo
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        String lendNo = lend.getLendNo();

        //填充所需数据
        List<Map<String, Object>> data = new ArrayList<>();
        for(LendItemReturn lendItemReturn : lendItemReturnList){
            Map<String, Object> map = new HashMap<>();
            map.put("agentProjectCode", lendNo);

            LendItem lendItem = lendItemMapper.selectById(lendItemReturn.getLendItemId());
            map.put("voteBillNo", lendItem.getLendItemNo());

            map.put("toBindCode", userBindService.getBindCodeByUserId(lendItemReturn.getInvestUserId()));
            map.put("transitAmt", lendItemReturn.getTotal());
            map.put("baseAmt", lendItemReturn.getPrincipal());
            map.put("benifitAmt", lendItemReturn.getInterest());
            map.put("feeAmt", new BigDecimal(0));

            data.add(map);
        }
        return data;
    }

}
