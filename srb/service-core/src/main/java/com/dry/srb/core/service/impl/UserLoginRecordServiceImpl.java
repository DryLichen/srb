package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.srb.core.pojo.entity.UserLoginRecord;
import com.dry.srb.core.mapper.UserLoginRecordMapper;
import com.dry.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Autowired
    private UserLoginRecordMapper userLoginRecordMapper;

    /**
     * 获取会员最近50次登录记录
     */
    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("id")
                .last("limit 50");

        List<UserLoginRecord> userLoginRecordList = userLoginRecordMapper.selectList(queryWrapper);
        return userLoginRecordList;
    }
}
