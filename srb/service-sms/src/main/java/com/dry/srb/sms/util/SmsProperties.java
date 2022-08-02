package com.dry.srb.sms.util;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 实现接口后，可以在拿到绑定值后给变量复制，防止空值
 */
@Data
@Component
@ConfigurationProperties(prefix = "ronglianyun.sms")
public class SmsProperties implements InitializingBean {
    private String accountSId;
    private String accountToken;
    private String appId;
    private String templateId;
    private String subAppend;
    private String reqId;

    public static String ACCOUNT_SID;
    public static String ACCOUNT_TOKEN;
    public static String APP_ID;
    public static String TEMPLATE_ID;
    public static String SUB_APPEND;
    public static String REQ_ID;

    // 当私有成员被赋值后，此方法自动被调用，从而初始化常量
    @Override
    public void afterPropertiesSet() throws Exception {
        ACCOUNT_SID = accountSId;
        ACCOUNT_TOKEN = accountToken;
        APP_ID = appId;
        TEMPLATE_ID = templateId;
        SUB_APPEND = subAppend;
        REQ_ID = reqId;
    }

}
