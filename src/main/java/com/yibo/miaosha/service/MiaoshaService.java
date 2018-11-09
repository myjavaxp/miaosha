package com.yibo.miaosha.service;

import com.yibo.miaosha.domain.MiaoshaOrder;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.MiaoshaKey;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MiaoshaService {
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisService redisService;

    @Autowired
    public MiaoshaService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
    }

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        if (goodsService.reduceStock(goods)) {
            return orderService.createOrder(user, goods);
        } else {
            redisService.set(MiaoshaKey.isGoodsOver, goods.getId().toString(), true);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public long getMiaoshaResult(long goodsId, long userId) {
        MiaoshaOrder one = orderService.getOne(userId, goodsId);
        if (one != null) {
            return one.getOrderId();
        } else {
            if (redisService.exists(MiaoshaKey.isGoodsOver, String.valueOf(goodsId))) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}