package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaOrder;
import com.yibo.miaosha.mq.Sender;
import com.yibo.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {
    private final Sender sender;

    @Autowired
    public DemoController(Sender sender) {
        this.sender = sender;
    }

    @GetMapping("/direct/{message}")
    public Result<String> message(@PathVariable String message) {
        sender.send(message);
        return Result.success(message);
    }

    @GetMapping("/topic/{message}")
    public Result<String> topic(@PathVariable String message) {
        sender.sendTopic(message);
        return Result.success(message);
    }

    @GetMapping("/fanout/{message}")
    public Result<String> fanout(@PathVariable String message) {
        sender.sendFanout(message);
        return Result.success(message);
    }

    @GetMapping("/header/{message}")
    public Result<String> header(@PathVariable String message) {
        sender.sendHeaders(message);
        return Result.success(message);
    }

    @GetMapping("/fast")
    public Result<List<MiaoshaOrder>> fast() {
        return Result.success(null);
    }
}