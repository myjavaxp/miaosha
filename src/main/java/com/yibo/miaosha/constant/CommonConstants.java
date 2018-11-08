package com.yibo.miaosha.constant;

import com.alibaba.fastjson.serializer.SerializerFeature;

public class CommonConstants {
    public static final SerializerFeature[] FEATURES = new SerializerFeature[]{
            SerializerFeature.PrettyFormat,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNonStringKeyAsString,
            SerializerFeature.WriteMapNullValue};
    public static final String TOPIC_EXCHANGE = "topic exchange";
    public static final String QUEUE_NAME = "direct";
    public static final String MIAOSHA_QUEUE = "miaosha queue";
    public static final String HEADERS_QUEUE = "headers queue";
    public static final String TOPIC_QUEUE1 = "topic1";
    public static final String TOPIC_QUEUE2 = "topic2";
    public static final String TOPIC_KEY1 = "topic.key1";
    public static final String TOPIC_KEY2 = "topic.#";
    public static final String FANOUT_EXCHANGE = "fanout exchange";
    public static final String HEADERS_EXCHANGE = "headers exchange";
}