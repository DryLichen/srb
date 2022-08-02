package com.dry.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.BusinessException;
import com.dry.srb.core.enums.LendStatusEnum;
import com.dry.srb.core.enums.ReturnMethodEnum;
import com.dry.srb.core.enums.TransTypeEnum;
import com.dry.srb.core.hfb.HfbConst;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.mapper.*;
import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.*;
import com.dry.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.dry.srb.core.pojo.vo.BorrowerDetailVO;
import com.dry.srb.core.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Slf4j
@Service
public class LendServiceImpl extends ServiceImpl<LendMapper, Lend> implements LendService {

    @Autowired
    private LendMapper lendMapper;
    @Autowired
    private BorrowerMapper borrowerMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private LendItemMapper lendItemMapper;

    @Autowired
    private DictService dictService;
    @Autowired
    private BorrowerService borrowerService;
    @Autowired
    private TransFlowService transFlowService;
    @Autowired
    private LendReturnService lendReturnService;

    /**
     * 生成标的记录
     */
    @Override
    public void createLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        // 创建标的对象
        Lend lend = new Lend();
        lend.setUserId(borrowInfo.getUserId());
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());  //生成标的编号
        lend.setTitle(borrowInfoApprovalVO.getTitle());
        lend.setAmount(borrowInfo.getAmount());
        lend.setPeriod(borrowInfo.getPeriod());
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate());  //从审批对象获取
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate());  //从审批对象获取
        lend.setReturnMethod(borrowInfo.getReturnMethod());

        lend.setLowestAmount(new BigDecimal(100));
        lend.setInvestAmount(new BigDecimal(0));
        lend.setInvestNum(0);

        // 设置时间，起始时间从审批对象中获取
        lend.setPublishDate(LocalDateTime.now());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dtf);
        lend.setLendStartDate(startDate);
        LocalDate endDate = startDate.plusMonths(lend.getPeriod());
        lend.setLendEndDate(endDate);

        lend.setLendInfo(borrowInfoApprovalVO.getLendInfo());

        // 预期收益
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, RoundingMode.DOWN);
        BigDecimal amount = lend.getAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        lend.setExpectAmount(amount);
        lend.setRealAmount(new BigDecimal(0));

        // 审核
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        lend.setCheckTime(LocalDateTime.now());
        lend.setCheckAdminId(1L);

        // 插入到数据库中
        lendMapper.insert(lend);
    }

    /**
     * 获取标的列表
     * 查找关键词为用户Id或是借款申请的Id
     */
    @Override
    public List<Lend> getList(Long keyword) {
        List<Lend> lendList = null;

        // 判断关键词是否为空
        if(keyword != null){
            QueryWrapper<Lend> lendQueryWrapper = new QueryWrapper<>();
            lendQueryWrapper.eq("user_id", keyword)
                    .or().eq("borrow_info_id", keyword);
            lendList = lendMapper.selectList(lendQueryWrapper);
        } else {
            lendList = lendMapper.selectList(null);
        }

        // 将值为数值的某些属性获取字符串值，放入map中
        lendList.forEach( lend -> {
            Map<String, Object> param = lend.getParam();
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
            param.put("returnMethod", returnMethod);
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            param.put("status", status);
        });

        return lendList;
    }

    /**
     * 获取标的详情
     */
    @Override
    public Map<String, Object> getLendDetail(Long id) {
        // 根据id获取标的对象
        Lend lend = lendMapper.selectById(id);
        Map<String, Object> param = lend.getParam();

        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", lend.getReturnMethod());
        param.put("returnMethod", returnMethod);
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        param.put("status", status);

        // 根据userId获取借款人对象
        Long userId = lend.getUserId();
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);

        BorrowerDetailVO borrowerDetail = borrowerService.getBorrowerDetailVOById(borrower.getId());

        // 组装标的详情数据
        Map<String, Object> lendDetail = new HashMap<>();
        lendDetail.put("lend", lend);
        lendDetail.put("borrower", borrowerDetail);

        return lendDetail;
    }

    /**
     * 计算投资总收益
     */
    @Override
    public BigDecimal getYield(BigDecimal investAmount, BigDecimal yearRate, Integer totalMonth, Integer returnMethod) {
        BigDecimal yield = null;

        if (returnMethod == ReturnMethodEnum.ONE.getMethod()) {
            yield = Amount1Helper.getInterestCount(investAmount, yearRate, totalMonth);
        } else if (returnMethod == ReturnMethodEnum.TWO.getMethod()) {
            yield = Amount2Helper.getInterestCount(investAmount, yearRate, totalMonth);
        } else if (returnMethod == ReturnMethodEnum.THREE.getMethod()) {
            yield = Amount3Helper.getInterestCount(investAmount, yearRate, totalMonth);
        } else {
            yield = Amount4Helper.getInterestCount(investAmount, yearRate, totalMonth);
        }

        return yield;
    }

    /**
     * 平台放款
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void makeLoan(Long lendId) {
        // 获取借款标的
        Lend lend = lendMapper.selectById(lendId);

        // 1.生成向汇付宝发起放款请求的参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentProjectCode", lend.getLendNo());  //放款项目编号
        paramMap.put("agentBillNo", LendNoUtils.getLoanNo()); //放款业务编号

        // 平台手续费
        // 平台收益，放款扣除，借款人借款实际金额=借款金额-平台收益
        BigDecimal monthServiceRate = lend.getServiceRate().divide(new BigDecimal(12), 8, RoundingMode.DOWN);
        BigDecimal mchFee = lend.getInvestAmount().multiply(monthServiceRate).multiply(new BigDecimal(lend.getPeriod()));
        paramMap.put("mchFee", mchFee);

        paramMap.put("note", null);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        // 2.发送请求并处理结果
        JSONObject result = RequestHelper.sendRequest(paramMap, HfbConst.MAKE_LOAD_URL);
        String voteAmt = result.getString("voteAmt");
        String agentBillNo = result.getString("agentBillNo");

        if(!"0000".equals(result.getString("resultCode"))){
            throw new BusinessException(result.getString("resultMsg"));
        }

        // 3.更新标的信息
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setRealAmount(mchFee);
        lend.setPaymentTime(LocalDateTime.now());
        lendMapper.updateById(lend);

        // 4.给借款账户转入资金
        Long userId = lend.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();
        userAccountMapper.updateAccountByBindCode(bindCode, new BigDecimal(voteAmt), new BigDecimal(0));

        // 5.添加借款人流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo, //放款流水号
                bindCode,
                new BigDecimal(voteAmt),
                TransTypeEnum.BORROW_BACK,
                "借款放款到账：" + lend.getLendNo());
        transFlowService.saveTransFlow(transFlowBO);

        // 6.解冻并扣除投资人资金
        List<LendItem> lendItemList = this.getLendItemListByLendId(lendId, 1);
        lendItemList.stream().forEach(lendItem -> {
            Long investUserId = lendItem.getInvestUserId();
            UserInfo investor = userInfoMapper.selectById(investUserId);
            String investorBindCode = investor.getBindCode();
            BigDecimal investAmount = lendItem.getInvestAmount();
            userAccountMapper.updateAccountByBindCode(investorBindCode, new BigDecimal(0), investAmount.negate());

            // 7.添加投资人资金流水
            TransFlowBO transFlowBO1 = new TransFlowBO(
                    LendNoUtils.getTransNo(), // 扣款解冻流水号
                    investorBindCode,
                    investAmount,
                    TransTypeEnum.INVEST_UNLOCK,
                    "冻结资金转出，出借放款，编号：" + lend.getLendNo());
            transFlowService.saveTransFlow(transFlowBO1);
        });

        // 8.生成借款人还款计划和投资人回款计划
        lendReturnService.repaymentLend(lend);
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
