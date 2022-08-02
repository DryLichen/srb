package com.dry.srb.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.base.dto.SmsDTO;
import com.dry.srb.core.enums.TransTypeEnum;
import com.dry.srb.core.enums.UserBindEnum;
import com.dry.srb.core.hfb.FormHelper;
import com.dry.srb.core.hfb.HfbConst;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.pojo.bo.TransFlowBO;
import com.dry.srb.core.pojo.entity.UserAccount;
import com.dry.srb.core.mapper.UserAccountMapper;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.service.TransFlowService;
import com.dry.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.service.UserBindService;
import com.dry.srb.core.util.LendNoUtils;
import com.dry.srb.rabbitmq.service.MQService;
import com.dry.srb.rabbitmq.util.MQConst;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private TransFlowService transFlowService;
    @Autowired
    private UserBindService userBindService;
    @Autowired
    private MQService mqService;

    /**
     * 会员账户充值
     * @return 用来向 汇付宝 发起充值请求的 表单
     */
    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        // 获取会员对象，判断账户绑定汇付宝状态
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Integer bindStatus = userInfo.getBindStatus();
        Assert.isTrue(bindStatus == UserBindEnum.BIND_OK.getStatus(), ResponseEnum.USER_NO_BIND_ERROR);

        // 构建表单参数map
        HashMap<String, Object> params = new HashMap<>();
        params.put("agentId", HfbConst.AGENT_ID);
        params.put("agentBillNo", LendNoUtils.getChargeNo());
        params.put("bindCode", userInfo.getBindCode());
        params.put("chargeAmt", chargeAmt);
        params.put("feeAmt", new BigDecimal(0));
        params.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        params.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        params.put("timestamp", RequestHelper.getTimestamp());
        params.put("sign", RequestHelper.getSign(params));

        // 生成充值请求的自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.RECHARGE_URL, params);
        return formStr;
    }

    /**
     * 充值回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notify(Map<String, Object> paramMap) {
        // 保证接口幂等性，如果流水号已经生成过则不再重复调用此接口
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean result = transFlowService.TransNoExists(agentBillNo);
        if(result){
            log.info("幂等性判断被调用");
            return "success";
        }

        // 更新账户表余额和冻结金额
        String bindCode = (String) paramMap.get("bindCode");
        String chargeAmt = (String) paramMap.get("chargeAmt");
        userAccountMapper.updateAccountByBindCode(bindCode, new BigDecimal(chargeAmt), new BigDecimal(0));

        // 增加交易流水
        TransFlowBO transFlowBO = new TransFlowBO();
        transFlowBO.setAgentBillNo(agentBillNo);
        transFlowBO.setBindCode(bindCode);
        transFlowBO.setAmount(new BigDecimal(chargeAmt));
        transFlowBO.setTransTypeEnum(TransTypeEnum.RECHARGE);
        transFlowBO.setMemo("充值");
        transFlowService.saveTransFlow(transFlowBO);

        // 向用户发送充值成功的短信
        log.info("发送充值成功短信ing......");
        String mobile = userBindService.getMobileByBindCode(bindCode);
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMobile(mobile);
        smsDTO.setMessage("充值成功");
        mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);

        log.info("用户充值异步回调充值成功：{}", JSON.toJSONString(paramMap));
        return "success";
    }

    /**
     * 获取用户账户余额
     */
    @Override
    public BigDecimal getAmount(Long userId) {
        QueryWrapper<UserAccount> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("amount").eq("user_id", userId).last("limit 1");
        BigDecimal amount = (BigDecimal) userAccountMapper.selectObjs(queryWrapper).get(0);

        return amount;
    }

    /**
     * @return 生成向汇付宝发起提现请求的表单
     */
    @Override
    public String withdraw(BigDecimal amount, Long userId) {
        // 判断账户余额是否足够提现
        BigDecimal balance = this.getAmount(userId);
        Assert.isTrue(balance.compareTo(amount) >= 0, ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        // 生成表单
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", userBindService.getBindCodeByUserId(userId));
        paramMap.put("fetchAmt", amount);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        String form = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return form;
    }

    /**
     * 提现回调
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String notifyWithdraw(Map<String, Object> paramMap) {
        // 接口幂等性控制
        String agentBillNo = (String) paramMap.get("agentBillNo");
        if(transFlowService.TransNoExists(agentBillNo)){
            log.warn("接口幂等性控制被调用");
            return "success";
        }

        // 更新用户账户余额
        String bindCode = (String) paramMap.get("bindCode");
        String fetchAmt = (String) paramMap.get("fetchAmt");
        userAccountMapper.updateAccountByBindCode(bindCode, new BigDecimal("-" + fetchAmt), new BigDecimal(0));

        // 添加提现流水
        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现"
        );
        transFlowService.saveTransFlow(transFlowBO);

        log.info("用户提现异步回调成功：{}", JSON.toJSONString(paramMap));
        return "success";
    }
}
