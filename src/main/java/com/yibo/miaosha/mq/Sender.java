package com.yibo.miaosha.mq;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.yibo.miaosha.constant.CommonConstants.*;

@Service
public class Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    public void send(Object message) {
        String msg = JSON.toJSONString(message, FEATURES);
        LOGGER.debug("发送消息:{}", msg);
        amqpTemplate.convertAndSend(QUEUE_NAME, msg);
    }

    public void sendTopic(Object message) {
        String msg = JSON.toJSONString(message, FEATURES);
        LOGGER.debug("发送topic消息:{}", message);
        amqpTemplate.convertAndSend(TOPIC_EXCHANGE, TOPIC_KEY1, msg + 1);
        amqpTemplate.convertAndSend(TOPIC_EXCHANGE, TOPIC_KEY2, msg + 2);
    }

    public void sendFanout(Object message) {
        String msg = JSON.toJSONString(message, FEATURES);
        LOGGER.debug("发送fanout消息:{}", message);
        amqpTemplate.convertAndSend(FANOUT_EXCHANGE, "", msg);
    }

    public void sendHeaders(Object message) {
        String msg = JSON.toJSONString(message, FEATURES);
        LOGGER.debug("发送fanout消息:{}", message);
        MessageProperties mp = new MessageProperties();
        mp.setHeader("header1", "value1");
        mp.setHeader("header2", "value2");
        mp.setHeader("header3", "value3");
        mp.setHeader("header4", "value4");
        Message m = new Message(msg.getBytes(), mp);
        amqpTemplate.convertAndSend(HEADERS_EXCHANGE, "", m);
    }

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = JSON.toJSONString(mm, FEATURES);
        LOGGER.debug("发送秒杀信息:{}", msg);
        amqpTemplate.convertAndSend(MIAOSHA_QUEUE, msg);
    }
}
