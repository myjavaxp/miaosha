package com.yibo.miaosha.redis.key;

public class OrderKey extends BasePrefix {
    private OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getMiaoshaOrderByUidGid = new OrderKey("moug");
}