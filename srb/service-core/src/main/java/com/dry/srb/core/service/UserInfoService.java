package com.dry.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.query.UserInfoQuery;
import com.dry.srb.core.pojo.vo.LoginVO;
import com.dry.srb.core.pojo.vo.RegisterVO;
import com.dry.srb.core.pojo.vo.UserIndexVO;
import com.dry.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 会员登录
     */
    UserInfoVO login(LoginVO loginVO, String ip);

    /**
     * 会员注册
     */
    void register(RegisterVO registerVO);

    /**
     * 分页查询数据
     */
    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    /**
     * 锁定或解锁账户
     */
    void lock(Long id, Integer status);

    /**
     * 在发送验证码之前检查手机号是否已经注册
     */
    boolean checkMobile(String mobile);

    /**
     * @return 获取用户个人中心首页展示信息
     */
    UserIndexVO getUserIndex(Long userId);
}
