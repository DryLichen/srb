package com.dry.srb.sms.receiver;

import com.dry.srb.base.dto.SmsDTO;
import com.dry.srb.rabbitmq.util.MQConst;
import com.dry.srb.sms.service.SmsService;
import com.dry.srb.sms.util.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听sms路径的消息队列
 */
@Slf4j
@Component
public class SmsReceiver {

    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO){
        log.info("SmsReceiver 消息监听");
        String[] params = new String[]{smsDTO.getMessage()};
        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_ID, params);
    }
}
