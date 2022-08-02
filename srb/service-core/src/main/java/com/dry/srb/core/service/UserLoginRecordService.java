package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {


    /**
     * 列出最后50次登录记录
     */
    List<UserLoginRecord> listTop50(Long userId);
}
