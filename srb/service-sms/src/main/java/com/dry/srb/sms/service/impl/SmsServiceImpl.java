package com.dry.srb.sms.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.dry.common.exception.Assert;
import com.dry.common.exception.BusinessException;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.sms.service.SmsService;
import com.dry.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public void send(String mobile, String templateId, String[] params) {
        /**
         *  1.创建远程连接
         */
        //生产环境请求地址
        String serverIp = "app.cloopen.com";
        String serverPort = "8883";
        //开发者主账号ACCOUNT SID，主账号令牌AUTH TOKEN，应用程序 appId
        String accountSId = SmsProperties.ACCOUNT_SID;
        String accountToken = SmsProperties.ACCOUNT_TOKEN;
        String appId = SmsProperties.APP_ID;

        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);

        String subAppend = SmsProperties.SUB_APPEND;  //可选 扩展码，四位数字 0~9999
        String reqId = SmsProperties.REQ_ID;  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复


        /**
         *  2.发送短信业务
         */
        try{
            // 向远程服务器发送短信请求
            HashMap<String, Object> result = sdk.sendTemplateSMS(mobile, templateId, params, subAppend, reqId);
            // 容联云响应失败，获取不到结果
            Assert.notNull(result, ResponseEnum.CLOOPEN_RESPONSE_FAIL);

            // 响应成功，有结果
            String statusCode = (String) result.get("statusCode");
            String statusMsg = (String) result.get("statusMsg");

            if("000000".equals(result.get("statusCode"))){
                log.info("容联云响应结果：");
                log.info("statusCode:{}", statusCode);
                log.info("statusMsg:{}", statusMsg);

                //正常返回输出data包体信息（map）
                HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
                Set<String> keySet = data.keySet();
                for(String key : keySet){
                    Object object = data.get(key);
                    System.out.println(key +" = "+object);
                }
            }else{
                //异常返回输出错误码和错误信息
                log.info("容联云响应结果：");
                log.info("错误码：{}", statusCode, "错误信息：{}", statusMsg);
            }
        } catch (Exception e){
            log.error("容联云发送短信SDK内部错误：{}", ExceptionUtils.getStackTrace(e));
            throw new BusinessException(ResponseEnum.CLOOPEN_SMS_ERROR, e);
        }
    }
}
