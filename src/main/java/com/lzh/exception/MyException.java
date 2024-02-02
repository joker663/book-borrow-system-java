package com.lzh.exception;

import lombok.Getter;

/**
 * @Description: 自定义异常
 * @Author: lzh
 * @Date: 2024-01-21
 */
@Getter
public class MyException extends RuntimeException{

    private String code;

    public MyException(String code, String msg) {
        super(msg);
        this.code = code;
    }

}
