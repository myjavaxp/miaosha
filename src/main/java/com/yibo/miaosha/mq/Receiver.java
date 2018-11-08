package com.yibo.miaosha.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.yibo.miaosha.constant.CommonConstants.*;

@Service
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    @RabbitListener(queues = QUEUE_NAME)
    public void receive(String message) {
        LOGGER.info("收到消息:{}", message);
    }

    @RabbitListener(queues = TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        LOGGER.info("topic1收到消息:{}", message);
    }

    @RabbitListener(queues = TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        LOGGER.info("topic2收到消息:{}", message);
    }

    @RabbitListener(queues = HEARDERS_QUEUE)
    public void receiveHeader(byte[] message) {
        LOGGER.info("header queue收到消息:{}", new String(message));
    }
}
