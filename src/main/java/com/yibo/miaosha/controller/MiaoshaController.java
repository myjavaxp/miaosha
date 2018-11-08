package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.mq.MiaoshaMessage;
import com.yibo.miaosha.mq.Sender;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.GoodsKey;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.service.OrderService;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisService redisService;
    private final Sender sender;


    @Autowired
    public MiaoshaController(GoodsService goodsService, OrderService orderService, RedisService redisService, Sender sender) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
        this.sender = sender;
    }

    /**
     * 返回秒杀结果
     *
     * @param goodsId 商品ID
     * @param user    由token来确定
     * @return 秒杀结果
     */
    @PostMapping("/do_miaosha")
    public Result<Void> miaosha(@RequestParam long goodsId, MiaoshaUser user) {
        if (user.getNickname().equals("游客")) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //判断库存
        Long stock = redisService.decr(GoodsKey.goodsStock, "" + goodsId);
        if (stock < 0) {
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判定是否重复秒杀
        if (orderService.getOne(user.getId(), goodsId) != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }
        MiaoshaMessage mm = new MiaoshaMessage(user, goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(null);
        /*if (user.getNickname().equals("游客")) {
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
        return "order_detail";*/
    }

    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> list = goodsService.listGoodsVo();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(e -> redisService.set(GoodsKey.goodsStock, e.getId().toString(), e.getStockCount()));
        }
    }
}
