package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.base.util.JwtUtils;
import com.dry.srb.core.mapper.UserAccountMapper;
import com.dry.srb.core.mapper.UserLoginRecordMapper;
import com.dry.srb.core.pojo.entity.UserAccount;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.pojo.entity.UserLoginRecord;
import com.dry.srb.core.pojo.query.UserInfoQuery;
import com.dry.srb.core.pojo.vo.LoginVO;
import com.dry.srb.core.pojo.vo.RegisterVO;
import com.dry.srb.core.pojo.vo.UserIndexVO;
import com.dry.srb.core.pojo.vo.UserInfoVO;
import com.dry.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private UserLoginRecordMapper userLoginRecordMapper;

    //会员注册
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterVO registerVO) {
        Integer userType = registerVO.getUserType();
        String mobile = registerVO.getMobile();
        String password = registerVO.getPassword();

        //判断手机号是否已经注册过
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        Long count = userInfoMapper.selectCount(queryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.MOBILE_EXIST_ERROR);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(userType);
        userInfo.setMobile(mobile);
        userInfo.setName(mobile);
        userInfo.setNickName(mobile);
        userInfo.setPassword(password);
        userInfo.setStatus(UserInfo.STATUS_NORMAL);
        // 从oss上传头像
        userInfo.setHeadImg("https://dry-srb-file.oss-cn-chengdu.aliyuncs.com/avatar/CourtRooms.png");

        //向数据库添加新注册用户信息
        userInfoMapper.insert(userInfo);

        //向数据库添加新用户账户信息
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfo.getId());
        userAccountMapper.insert(userAccount);
    }

    // 会员登录
    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();

        //判断用户是否已经注册
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile)
                .eq("user_type", userType)
                .last("limit 1");
        Long count = userInfoMapper.selectCount(queryWrapper);
        Assert.isTrue(count != 0, ResponseEnum.LOGIN_MOBILE_ERROR);

        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        //判断密码是否正确
        Assert.equals(userInfo.getPassword(), password, ResponseEnum.LOGIN_PASSWORD_ERROR);

        //判断用户是否被锁定
        Assert.isTrue(userInfo.getStatus() == 1, ResponseEnum.LOGIN_LOKED_ERROR);

        //记录登录日志
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecord.setIp(ip);
        userLoginRecordMapper.insert(userLoginRecord);

        //以上校验均通过后，将token封装到 userInfoVo 中
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setToken(token);
        BeanUtils.copyProperties(userInfo, userInfoVO);

        return userInfoVO;
    }

    // 分页查询
    @Override
    public IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery) {
        // 如果没有筛选条件，则直接查询
        if(userInfoQuery == null){
            return userInfoMapper.selectPage(pageParam, null);
        }

        String mobile = userInfoQuery.getMobile();
        Integer userType = userInfoQuery.getUserType();
        Integer status = userInfoQuery.getStatus();

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(StringUtils.hasLength(mobile), "mobile", mobile)
                .eq(userType != null, "user_type", userType)
                .eq(status != null, "status", status);

        IPage<UserInfo> pageModel = userInfoMapper.selectPage(pageParam, queryWrapper);
        return pageModel;
    }

    /**
     * 锁定或解锁账户
     */
    @Override
    public void lock(Long id, Integer status) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setStatus(status);
        userInfoMapper.updateById(userInfo);
    }

    // 检查手机号是否已经注册
    @Override
    public boolean checkMobile(String mobile) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile", mobile);
        Long count = userInfoMapper.selectCount(queryWrapper);

        return count > 0;
    }

    /**
     * @return 获取用户个人中心首页展示信息
     */
    @Override
    public UserIndexVO getUserIndex(Long userId) {
        UserIndexVO userIndexVO = new UserIndexVO();

        UserInfo userInfo = userInfoMapper.selectById(userId);
        BeanUtils.copyProperties(userInfo, userIndexVO);

        userIndexVO.setUserId(userId);

        UserAccount userAccount = userAccountMapper.selectById(userId);
        userIndexVO.setAmount(userAccount.getAmount());
        userIndexVO.setFreezeAmount(userAccount.getFreezeAmount());

        QueryWrapper<UserLoginRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("id")
                .last("limit 1");
        UserLoginRecord userLoginRecord = userLoginRecordMapper.selectOne(queryWrapper);
        userIndexVO.setLastLoginTime(userLoginRecord.getCreateTime());

        return userIndexVO;
    }
}
