package com.wy.common.config.exception;

import com.wy.common.result.Result;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result error() {
        return Result.fail().message("全局异常信息");
    }

    @ExceptionHandler(ArithmeticException.class)
    public Result<Object> error(ArithmeticException e){
        e.printStackTrace();
        return Result.fail().message("特定异常信息");
    }
}
