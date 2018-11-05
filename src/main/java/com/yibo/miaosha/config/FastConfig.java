package com.yibo.miaosha.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static com.yibo.miaosha.constant.RedisConstants.FEATURES;

@Configuration
public class FastConfig {
    @Bean
    public HttpMessageConverters fastJsonConfigure() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(FEATURES);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        converter.setFastJsonConfig(config);
        return new HttpMessageConverters(converter);
    }
}
