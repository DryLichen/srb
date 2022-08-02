package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.UserBind;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.UserBindVO;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface UserBindService extends IService<UserBind> {

    /**
     * 提交用户绑定数据到srb数据库，汇付宝的数据提交由汇付宝自己负责
     * @param userBindVO 网页的绑定请求数据
     * @param userId 用户账户id
     * @return 返回提交数据需要的表格，在网页端被自动提交，因为有js代码执行
     */
    String commitBindUser(UserBindVO userBindVO, Long userId);

    /**
     * 绑定汇付宝回调方法
     * 修改user_bind和user_account表绑定相关的数据
     */
    void notify(Map<String, Object> map);

    /**
     * 根据用户id获取绑定码
     */
    String getBindCodeByUserId(Long userId);

    /**
     * @return 根据绑定码获取用户手机号
     */
    String getMobileByBindCode(String bindCode);
}
