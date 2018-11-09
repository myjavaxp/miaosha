package com.yibo.miaosha.service;

import com.yibo.miaosha.domain.MiaoshaOrder;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.domain.OrderInfo;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.MiaoshaKey;
import com.yibo.miaosha.util.Md5Util;
import com.yibo.miaosha.util.UUIDUtil;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
@Transactional
public class MiaoshaService {
    private static final char[] OPS = new char[]{'+', '-', '*'};
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final RedisService redisService;

    @Autowired
    public MiaoshaService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;
    }

    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        if (goodsService.reduceStock(goods)) {
            return orderService.createOrder(user, goods);
        } else {
            redisService.set(MiaoshaKey.isGoodsOver, goods.getId().toString(), true);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public long getMiaoshaResult(long goodsId, long userId) {
        MiaoshaOrder one = orderService.getOne(userId, goodsId);
        if (one != null) {
            return one.getOrderId();
        } else {
            if (redisService.exists(MiaoshaKey.isGoodsOver, String.valueOf(goodsId))) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if (user.getNickname().equals("游客") || path == null) {
            return false;
        }
        String pathOld = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
        if (user.getNickname().equals("游客") || goodsId < 1) {
            return null;
        }
        String str = Md5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, str);
        return str;
    }

    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if (user.getNickname().equals("游客") || goodsId < 1) {
            return null;
        }
        int width = 100;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if (user.getNickname().equals("游客") || goodsId < 1) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode != 0) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * + - *
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = OPS[rdm.nextInt(3)];
        char op2 = OPS[rdm.nextInt(3)];
        return "" + num1 + op1 + num2 + op2 + num3;
    }
}