package com.yibo.miaosha.controller;

import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @Resource
    private RedisService redisService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/hello")
    @ResponseBody
    public Result<String> hello() {
        return Result.success("hello,yibo");
    }

    @GetMapping("/error")
    @ResponseBody
    public Result<Void> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @GetMapping("/page")
    public String page(Model model) {
        model.addAttribute("name", "Yibo");
        return "hello";
    }
}