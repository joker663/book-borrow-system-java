package com.lzh.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description: createTime、updateTime字段填充
 * @Author: lzh
 * @Date: 2024-01-28
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时的填充策略
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
//        this.setFieldValByName("createBy", Objects.requireNonNull(TokenUtil.getCurrentUser()).getId(),metaObject);
//        this.setFieldValByName("updateBy", Objects.requireNonNull(TokenUtil.getCurrentUser()).getId(),metaObject);
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
//        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
//        this.setFieldValByName("updateBy", Objects.requireNonNull(TokenUtil.getCurrentUser()).getId(),metaObject);
    }
}
