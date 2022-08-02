package com.dry.srb.rabbitmq.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public boolean sendMessage(String exchange, String routineKey, Object message){
        log.info("RabbitMQ发送消息");
        amqpTemplate.convertAndSend(exchange, routineKey, message);
        return true;
    }
}
