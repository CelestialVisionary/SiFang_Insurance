package com.sifang.insurance.payment.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.cloud.stream.converter.MessageConverterUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息配置类
 * 配置消息转换器等
 */
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    /**
     * 配置FastJson消息转换器
     */
    @Bean
    public HttpMessageConverter fastJsonHttpMessageConverters() {
        // 创建FastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        
        // 创建FastJson配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue, // 输出null值
                SerializerFeature.WriteNullStringAsEmpty, // 空字符串输出空串
                SerializerFeature.WriteNullNumberAsZero, // 空数字输出0
                SerializerFeature.WriteNullListAsEmpty, // 空集合输出空数组
                SerializerFeature.WriteNullBooleanAsFalse, // 空布尔值输出false
                SerializerFeature.PrettyFormat, // 格式化输出
                SerializerFeature.WriteDateUseDateFormat // 日期格式化
        );
        
        // 设置字符集
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        
        // 设置消息转换器的配置
        fastConverter.setFastJsonConfig(fastJsonConfig);
        
        // 设置支持的媒体类型
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(mediaTypes);
        
        return fastConverter;
    }

    /**
     * 添加消息转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverters());
    }

    /**
     * 配置String消息转换器
     * 用于处理消息队列中的字符串消息
     */
    @Bean
    public MessageConverter stringMessageConverter() {
        StringMessageConverter converter = new StringMessageConverter(StandardCharsets.UTF_8);
        return converter;
    }
}