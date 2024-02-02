package com.lzh.common;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @Description: 状态枚举常量
 * @Author: lzh
 * @Date: 2024-01-21
 */
@Getter
@ApiModel(value = "状态枚举常量")
public enum StatueEnum {

    STATUE_TRUE(true),

    STATUE_FALSE(false);

    private Boolean statue;

    StatueEnum(Boolean statue) {
        this.statue = statue;
    }

}
