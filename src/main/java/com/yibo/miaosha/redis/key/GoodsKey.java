package com.yibo.miaosha.redis.key;

public class GoodsKey extends BasePrefix {

    private GoodsKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey goodsStock = new GoodsKey(0L, "gs");
}
