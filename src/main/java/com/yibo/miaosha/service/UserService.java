package com.yibo.miaosha.service;

import com.yibo.miaosha.dao.MiaoshaUserMapper;
import com.yibo.miaosha.domain.MiaoshaUser;
import com.yibo.miaosha.redis.RedisService;
import com.yibo.miaosha.redis.key.GoodsKey;
import com.yibo.miaosha.redis.key.MiaoshaUserKey;
import com.yibo.miaosha.util.Md5Util;
import com.yibo.miaosha.util.UUIDUtil;
import com.yibo.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserService {
    private final MiaoshaUserMapper miaoshaUserMapper;
    private final RedisService redisService;
    private final StringRedisTemplate stringRedisTemplate;
    private final GoodsService goodsService;

    @Autowired
    public UserService(MiaoshaUserMapper miaoshaUserMapper, RedisService redisService, StringRedisTemplate stringRedisTemplate, GoodsService goodsService) {
        this.miaoshaUserMapper = miaoshaUserMapper;
        this.redisService = redisService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.goodsService = goodsService;
    }

    @Cacheable(cacheNames = "miaoshaUser", key = "''+#id")
    public MiaoshaUser getById(long id) {
        return miaoshaUserMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public void init(int count) throws IOException {
        miaoshaUserMapper.initUser();
        miaoshaUserMapper.initGoods();
        miaoshaUserMapper.initMiaoshaGoods();
        miaoshaUserMapper.initOrder();
        miaoshaUserMapper.initMiaoshaOrder();
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.flushDb();
            connection.flushAll();
            return null;
        });
        List<GoodsVo> list = goodsService.listGoodsVo();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(e -> redisService.set(GoodsKey.goodsStock, e.getId().toString(), e.getStockCount()));
        }
        List<MiaoshaUser> users = new ArrayList<>(count);
        int r = new Random().nextInt(100000);
        //生成用户
        for (int i = 0; i < count; i++) {
            MiaoshaUser user = new MiaoshaUser();
            user.setId(13000000000L + r + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(Md5Util.inputPassToDbPass("123456", user.getSalt()));
            users.add(user);
        }
        miaoshaUserMapper.addUsers(users);
        File file = new File("/Users/yibo/IdeaProjects/miaosha/src/main/resources/jmeter/token.txt");
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("无法删除旧文件");
            }
        }
        if (!file.createNewFile()) {
            throw new RuntimeException("无法新建文件");
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        for (MiaoshaUser user : users) {
            String token = UUIDUtil.uuid();
            redisService.set(MiaoshaUserKey.token, token, user);
            raf.seek(raf.length());
            String row = user.getId() + "," + token;
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();
    }
}
