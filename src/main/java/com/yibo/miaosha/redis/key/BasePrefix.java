package com.yibo.miaosha.redis.key;

public abstract class BasePrefix implements KeyPrefix {

    private long expireSeconds;

    private String prefix;

    BasePrefix(String prefix) {//0代表永不过期
        this.prefix = prefix;
    }

    BasePrefix(long expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public long expireSeconds() {//默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        return getClass().getSimpleName() + ":" + prefix;
    }
}
