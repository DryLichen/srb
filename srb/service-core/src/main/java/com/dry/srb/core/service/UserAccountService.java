package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface UserAccountService extends IService<UserAccount> {

    /**
     * 会员账户充值
     * @return 返回用来向 汇付宝 发起充值请求的 表单
     */
    String commitCharge(BigDecimal chargeAmt, Long userId);

    /**
     * 账户充值回调方法
     */
    String notify(Map<String, Object> paramMap);

    /**
     * 获取用户账户余额
     */
    BigDecimal getAmount(Long userId);

    /**
     * @return 生成向汇付宝发起提现请求的表单
     */
    String withdraw(BigDecimal amount, Long userId);

    /**
     * 提现回调
     */
    String notifyWithdraw(Map<String, Object> paramMap);
}
