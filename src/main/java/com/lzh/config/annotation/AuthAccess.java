package com.lzh.config.annotation;

import java.lang.annotation.*;

/**
 * @Description: 如果方法上加这个注解，那么这些数据是不需要登录认证的
 * @Author: lzh
 * @Date: 2024-02-01
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthAccess {

}
