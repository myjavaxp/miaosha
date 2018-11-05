package com.yibo.miaosha.constant;

import com.alibaba.fastjson.serializer.SerializerFeature;

public class RedisConstants {
    public static final SerializerFeature[] FEATURES = new SerializerFeature[]{
            SerializerFeature.PrettyFormat,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.WriteMapNullValue};
}