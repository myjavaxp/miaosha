package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.service.MiaoshaService;
import com.yibo.miaosha.service.OrderService;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final MiaoshaService miaoshaService;

    @Autowired
    public MiaoshaController(GoodsService goodsService, OrderService orderService, MiaoshaService miaoshaService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.miaoshaService = miaoshaService;
    }

    @PostMapping("/do_miaosha")
    public String miaosha(@RequestParam long goodsId, Model model, MiaoshaUser user) {
        if (user.getNickname().equals("游客")) {
            return "login";
        }
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        if (goods.getStockCount() < 1) {
            model.addAttribute("error", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }
        if (orderService.getOne(user.getId(), goods.getId()) != null) {
            model.addAttribute("error", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }
}
