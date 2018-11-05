package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private final MiaoshaUserService userService;

    private final RedisService redisService;

    @Autowired
    public GoodsController(MiaoshaUserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

    @RequestMapping("/to_list")
    public String list(Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        return "goods_list";
    }
}