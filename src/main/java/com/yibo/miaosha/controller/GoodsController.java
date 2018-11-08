package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.GoodsKey;
import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.vo.GoodsDetailVo;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 自增长id可以用SnowFlake算法
 *
 * @author yibo
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    private final GoodsService goodsService;
    private final ThymeleafViewResolver thymeleafViewResolver;
    private final RedisService redisService;

    @Autowired
    public GoodsController(GoodsService goodsService, ThymeleafViewResolver thymeleafViewResolver, RedisService redisService) {
        this.goodsService = goodsService;
        this.thymeleafViewResolver = thymeleafViewResolver;
        this.redisService = redisService;
    }

    @GetMapping("/to_list")
    @ResponseBody
    public String list(Model model, MiaoshaUser user, HttpServletRequest request, HttpServletResponse response) {
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("user", user);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @GetMapping("/to_detail/{id}")
    @ResponseBody
    public String detail(@PathVariable long id, Model model, MiaoshaUser user, HttpServletResponse response, HttpServletRequest request) {
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + id, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        GoodsVo goods = goodsService.getGoodsVoById(id);
        long start = goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus;
        long remainSeconds;
        if (now < start) {
            miaoshaStatus = 0;
            remainSeconds = (start - now) / 1000;
        } else if (now > end) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            remainSeconds = 0;
            miaoshaStatus = 1;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);
        model.addAttribute("goods", goods);
        WebContext ctx = new WebContext(
                request,
                response,
                request.getServletContext(),
                request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + id, html);
        }
        return html;
    }

    @GetMapping(value = "/detail/{goodsId}")
    @ResponseBody
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