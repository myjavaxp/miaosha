package com.yibo.miaosha.service;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class MiaoshaService {
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        goodsService.reduceStock(goods);
        return orderService.createOrder(user, goods);
    }
}