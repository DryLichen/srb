package com.dry.srb.core.mapper;

import com.dry.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 根据 绑定码 更新 账户余额和冻结余额
     */
    void updateAccountByBindCode(@Param("bindCode") String bindCode,
                                 @Param("amount") BigDecimal amount,
                                 @Param("freezeAmount") BigDecimal freezeAmount);
}
