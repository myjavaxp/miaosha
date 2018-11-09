package com.yibo.miaosha.redis.key;

public class MiaoshaUserKey extends BasePrefix {

    private static final long TOKEN_EXPIRE = 30 * 60L;

    private MiaoshaUserKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE, "tk");
}
