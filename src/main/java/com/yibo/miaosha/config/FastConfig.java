package com.yibo.miaosha.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.yibo.miaosha.constant.CommonConstants;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.util.Arrays;

@Configuration
public class FastConfig {
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new com.alibaba.fastjson.support.config.FastJsonConfig();
        config.setSerializerFeatures(CommonConstants.FEATURES);
        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        return new HttpMessageConverters(converter);
    }
}
