package com.yibo.miaosha.service;

import com.yibo.miaosha.dao.MiaoshaOrderMapper;
import com.yibo.miaosha.dao.OrderInfoMapper;
import com.yibo.miaosha.domain.MiaoshaOrder;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.OrderKey;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final MiaoshaOrderMapper miaoshaOrderMapper;
    private final OrderInfoMapper orderInfoMapper;
    private final RedisService redisService;

    @Autowired
    public OrderService(MiaoshaOrderMapper miaoshaOrderMapper, OrderInfoMapper orderInfoMapper, RedisService redisService) {
        this.miaoshaOrderMapper = miaoshaOrderMapper;
        this.orderInfoMapper = orderInfoMapper;
        this.redisService = redisService;
    }

    @Cacheable(cacheNames = "alreadyBuy", key = "#userId+':'+#goodsId")
    public MiaoshaOrder getOne(long userId, long goodsId) {
        return miaoshaOrderMapper.getOne(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(Byte.valueOf("1"));
        orderInfo.setUserId(user.getId());
        orderInfoMapper.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder(user.getId(), orderInfo.getId(), goods.getId());
        miaoshaOrderMapper.insertSelective(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, user.getId() + "_" + goods.getId(), miaoshaOrder);
        return orderInfo;
    }
}