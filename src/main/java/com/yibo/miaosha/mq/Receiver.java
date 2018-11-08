package com.yibo.miaosha.mq;

import com.alibaba.fastjson.JSON;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.service.MiaoshaService;
import com.yibo.miaosha.service.OrderService;
import com.yibo.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yibo.miaosha.constant.CommonConstants.*;

@Service
public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final MiaoshaService miaoshaService;

    @Autowired
    public Receiver(GoodsService goodsService, OrderService orderService, MiaoshaService miaoshaService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.miaoshaService = miaoshaService;
    }

    @RabbitListener(queues = MIAOSHA_QUEUE)
    public void receiveMiaosha(String message) {
        LOGGER.info("收到秒杀消息:{}", message);
        MiaoshaMessage mm = JSON.parseObject(message, MiaoshaMessage.class);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        if (goods.getStockCount() < 1) {
            return;
        }
        if (orderService.getOne(user.getId(), goods.getId()) != null) {
            return;
        }
        miaoshaService.miaosha(user, goods);
    }

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

    @RabbitListener(queues = HEADERS_QUEUE)
    public void receiveHeader(byte[] message) {
        LOGGER.info("header queue收到消息:{}", new String(message));
    }
}
