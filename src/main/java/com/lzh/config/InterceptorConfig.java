package com.lzh.config;

import com.lzh.interceptor.JwtInterceptor;
import com.lzh.interceptor.ReaderJwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 拦截器配置类
 * @Author: lzh
 * @Date: 2024-01-27
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 用户端拦截器
        registry.addInterceptor(readerJwtInterceptor())
                .addPathPatterns("/front/collect/**","/front/myborrow/**")
                .excludePathPatterns("/front/auth/login",
                        "/front/auth/register",
                        "/front/index/**",
                        "/book/recommend",
                        "/book/change/recommend");

        // 管理端拦截器
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/home/**","/book/**","/borrow/**","/carousel/**",
                        "/category/**","/user/**","/role/**","/reader/**","/menu/**","/log/**")
                .excludePathPatterns("/auth/login",
                        "/auth/register",
                        "/**/export/**",
                        "/**/import/**",
                        "/file/**",
                        "/front/index/**",
                        "/book/recommend",
                        "/book/change/recommend");
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {// 将JwtInterceptor拦截器让Spring管理
        return new JwtInterceptor();
    }
    @Bean
    public ReaderJwtInterceptor readerJwtInterceptor() {// 将ReaderJwtInterceptor拦截器让Spring管理
        return new ReaderJwtInterceptor();
    }

}
