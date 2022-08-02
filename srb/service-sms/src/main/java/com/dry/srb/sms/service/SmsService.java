package com.dry.srb.sms.service;

/**
 * 发送短信服务接口
 */
public interface SmsService {

    void send(String mobile, String templateId, String[] params);
}
