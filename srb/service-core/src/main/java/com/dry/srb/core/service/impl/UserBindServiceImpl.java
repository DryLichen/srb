package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.enums.UserBindEnum;
import com.dry.srb.core.hfb.FormHelper;
import com.dry.srb.core.hfb.HfbConst;
import com.dry.srb.core.hfb.RequestHelper;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.pojo.entity.UserBind;
import com.dry.srb.core.mapper.UserBindMapper;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.pojo.vo.UserBindVO;
import com.dry.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Autowired
    private UserBindMapper userBindMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    //会员账户绑定汇付宝
    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        // 1.判断身份证号是否已经被其他账户绑定
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card", userBindVO.getIdCard())
                .ne("user_id", userId)
                .last("limit 1");
        Long count = userBindMapper.selectCount(userBindQueryWrapper);
        Assert.isTrue(count == 0, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        // 2.判断数据库是否已经有绑定记录
        userBindQueryWrapper = new QueryWrapper<UserBind>();
        userBindQueryWrapper.eq("user_id", userId);
        UserBind userBind = userBindMapper.selectOne(userBindQueryWrapper);

        if(userBind == null){
            //从未绑定过
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());

            userBindMapper.insert(userBind);
        } else {
            //用户点击了开户，但没有完成跳转到汇付宝后的绑定步骤，则更新重新填写的绑定数据
            BeanUtils.copyProperties(userBindVO, userBind);
            userBindMapper.updateById(userBind);
        }

        // 3.构建绑定汇付宝需要的自动提交表单
        Map<String, Object> map = new HashMap<>();
        map.put("agentId", HfbConst.AGENT_ID);
        map.put("agentUserId", userId);
        map.put("idCard", userBind.getIdCard());
        map.put("personalName", userBind.getName());
        map.put("bankType", userBind.getBankType());
        map.put("bankNo", userBind.getBankNo());
        map.put("mobile", userBind.getMobile());
        map.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        map.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        map.put("timestamp", RequestHelper.getTimestamp());
        //给请求添加签名
        map.put("sign", RequestHelper.getSign(map));
        //生成表单
        String form = FormHelper.buildForm(HfbConst.USERBIND_URL, map);

        return form;
    }

    //更新尚融宝表中由汇付宝生成的相关数据
    @Transactional( rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> map) {
        String bindCode = (String) map.get("bindCode");
        String agentUserId = (String) map.get("agentUserId");

        // 1.更新user_bind表
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", agentUserId);

        UserBind userBind = userBindMapper.selectOne(queryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        userBindMapper.updateById(userBind);

        // 2.更新user_info表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindCode(bindCode);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据用户id获取绑定码
     */
    @Override
    public String getBindCodeByUserId(Long userId) {
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        UserBind userBind = userBindMapper.selectOne(queryWrapper);

        return userBind.getBindCode();
    }

    /**
     * @return 根据绑定码获取用户手机号
     */
    @Override
    public String getMobileByBindCode(String bindCode) {
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bind_code", bindCode);
        UserBind userBind = userBindMapper.selectOne(queryWrapper);
        return userBind.getMobile();
    }


}
