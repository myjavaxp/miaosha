package com.yibo.miaosha.service;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MiaoshaService {
    private final GoodsService goodsService;
    private final OrderService orderService;

    @Autowired
    public MiaoshaService(GoodsService goodsService, OrderService orderService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
    }

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        goodsService.reduceStock(goods);
        return orderService.createOrder(user, goods);
    }
}