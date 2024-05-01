package com.lzh.annotation;

import java.lang.annotation.*;

/**
 * @Description: AOP实现日志
 * @Author: lzh
 * @Date: 2024-05-01
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    String value() default "";

    String role() default "";
}
