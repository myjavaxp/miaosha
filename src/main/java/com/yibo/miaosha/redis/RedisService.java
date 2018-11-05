package com.yibo.miaosha.redis;

import com.alibaba.fastjson.JSON;
import com.yibo.miaosha.redis.key.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.yibo.miaosha.constant.RedisConstants.FEATURES;

/**
 * 一个适用于key-value的Redis工具类
 */
@Service
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    private final ValueOperations<String, String> ops;

    @Autowired
    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.ops = stringRedisTemplate.opsForValue();
    }

    public <T> T get(String key, Class<T> clazz) {
        String value = ops.get(key);
        return JSON.parseObject(value, clazz);
    }

    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        String value = ops.get(prefix.getPrefix() + key);
        return JSON.parseObject(value, clazz);
    }

    public <T> void set(String key, T value) {
        ops.set(key, JSON.toJSONString(value, FEATURES));
    }

    public <T> void set(KeyPrefix prefix, String key, T value) {
        long expire = prefix.expireSeconds();
        String realKey = prefix.getPrefix() + key;
        if (expire > 0) {
            ops.set(realKey, JSON.toJSONString(value, FEATURES), expire, TimeUnit.SECONDS);
        } else {
            ops.set(realKey, JSON.toJSONString(value, FEATURES));
        }
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void delete(KeyPrefix prefix, String key) {
        stringRedisTemplate.delete(prefix.getPrefix() + key);
    }

    public boolean exists(String key) {
        Boolean flag = stringRedisTemplate.hasKey(key);
        return flag != null && flag;
    }

    public boolean exists(KeyPrefix prefix, String key) {
        Boolean flag = stringRedisTemplate.hasKey(prefix.getPrefix() + key);
        return flag != null && flag;
    }

    /**
     * 增加值
     */
    public Long incr(KeyPrefix prefix, String key) {
        return ops.increment(prefix.getPrefix() + key);
    }

    public Long incr(String key) {
        return ops.increment(key);
    }

    /**
     * 减少值
     */
    public Long decr(KeyPrefix prefix, String key) {
        return ops.decrement(prefix.getPrefix() + key);
    }

    public Long decr(String key) {
        return ops.decrement(key);
    }
}
