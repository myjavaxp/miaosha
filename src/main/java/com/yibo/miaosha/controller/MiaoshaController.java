package com.yibo.miaosha.controller;

import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.mq.MiaoshaMessage;
import com.yibo.miaosha.mq.Sender;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.GoodsKey;
import com.yibo.miaosha.result.CodeMsg;
import com.yibo.miaosha.result.Result;
import com.yibo.miaosha.service.GoodsService;
import com.yibo.miaosha.service.MiaoshaService;
import com.yibo.miaosha.service.OrderService;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisService redisService;
    private final Sender sender;
    private final MiaoshaService miaoshaService;

    @Autowired
    public MiaoshaController(GoodsService goodsService, OrderService orderService, RedisService redisService, Sender sender, MiaoshaService miaoshaService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
        this.sender = sender;
        this.miaoshaService = miaoshaService;
    }

    /**
     * 返回秒杀结果
     *
     * @param goodsId 商品ID
     * @param user    由token来确定
     * @return 秒杀结果
     */
    @PostMapping("/{path}/do_miaosha")
    public Result<Void> miaosha(
            @RequestParam long goodsId,
            MiaoshaUser user,
            @PathVariable String path) {
        if (user.getNickname().equals("游客")) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = miaoshaService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //判断库存
        long stock = redisService.decr(GoodsKey.goodsStock, "" + goodsId);
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

    @GetMapping("/result")
    public Result<Long> result(@RequestParam long goodsId, MiaoshaUser user) {
        if (user.getNickname().equals("游客")) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        return Result.success(miaoshaService.getMiaoshaResult(goodsId, user.getId()));
    }

    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> list = goodsService.listGoodsVo();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(e -> redisService.set(GoodsKey.goodsStock, e.getId().toString(), e.getStockCount()));
        }
    }

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoshaUser user,
                                         @RequestParam long goodsId,
                                         @RequestParam(defaultValue = "0") int verifyCode) {
        if (user.getNickname().equals("游客")) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }


    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<Void> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                            @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
