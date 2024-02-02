package com.lzh.config;

import com.lzh.config.interceptor.JwtInterceptor;
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
        registry.addInterceptor(jwtInterceptor())// 取Spring容器中的拦截器对象
                .addPathPatterns("/**")  // 拦截所有请求，通过判断token是否合法来决定是否需要登录
                .excludePathPatterns("/auth/login",
                        "/auth/register",
                        "/**/export/**",
                        "/**/import/**",
                        "/file/**");
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {// 将JwtInterceptor拦截器让Spring管理
        return new JwtInterceptor();
    }

}
