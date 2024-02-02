package com.lzh.exception;

import com.lzh.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: 全局异常处理
 * @Author: lzh
 * @Date: 2024-01-21
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 如果抛出的的是MyException，则调用该方法
     * @param myException 业务异常
     * @return Result
     */
    @ExceptionHandler(MyException.class)
    public Result handle(MyException myException){
        return Result.error(myException.getCode(), myException.getMessage());
    }

}
