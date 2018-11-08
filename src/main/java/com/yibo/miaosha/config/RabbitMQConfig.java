package com.yibo.miaosha.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.yibo.miaosha.constant.CommonConstants.*;

/**
 * 后台方式启动RabbitMQ
 * sudo rabbitmq-server -detached
 * 启用web控制台
 * rabbitmq-plugins enable rabbitmq_management
 * 注意默认情况下guest用户无法远程访问rabbitmq
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue miaoshaQueue() {
        return new Queue(MIAOSHA_QUEUE, true);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2, true);
    }

    @Bean
    public Queue headerQueue() {
        return new Queue(HEADERS_QUEUE, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(TOPIC_KEY1);
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(TOPIC_KEY2);
    }

    @Bean
    public Binding binding3() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding binding4() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    @Bean
    public Binding binding5() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("header1", "value1");
        map.put("header2", "value2");
        map.put("header3", "value3");
        map.put("header4", "value4");
        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }
}