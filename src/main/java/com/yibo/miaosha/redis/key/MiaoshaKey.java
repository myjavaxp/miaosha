package com.yibo.miaosha.redis.key;

public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
