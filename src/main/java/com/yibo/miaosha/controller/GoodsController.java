package com.yibo.miaosha.controller;

import com.yibo.miaosha.access.AccessLimit;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.vo.GoodsDetailVo;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 自增长id可以用SnowFlake算法
 *
 * @author yibo
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("/to_list")
    @AccessLimit(seconds = 20, maxCount = 5, needLogin = false)
    public Result<List<GoodsVo>> list() {
        return Result.success(goodsService.listGoodsVo());
    }

    @GetMapping(value = "/detail/{goodsId}")
    public Result<GoodsDetailVo> detail(MiaoshaUser user,
                                        @PathVariable long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoById(goodsId);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus;
        long remainSeconds;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (startAt - now) / 1000;
        } else if (now > endAt) {//秒杀已经结束
            remainSeconds = -1;
            miaoshaStatus = 2;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }
}