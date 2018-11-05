package com.yibo.miaosha.redis.key;

public interface KeyPrefix {
    long expireSeconds();

    String getPrefix();
}
