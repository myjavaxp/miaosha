package com.yibo.miaosha.redis.key;

public class GoodsKey extends BasePrefix {

    private GoodsKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60L, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60L, "gd");
}
