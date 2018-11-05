package com.yibo.miaosha.redis.key;

public class UserKey extends BasePrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey ID = new UserKey("id");
    public static UserKey NAME = new UserKey("name");
}