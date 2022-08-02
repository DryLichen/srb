package com.dry.common.exception;

import com.dry.common.result.ResponseEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常类，可以处理多个错误响应码，不必一个一个写exceptionHandler
 */
@Data
@NoArgsConstructor
public class BusinessException extends RuntimeException{
    private Integer code;
    private String message;


    /**
     * 通过 错误消息 构造异常对象
     */
    public BusinessException(String message) {
        this.message = message;
    }

    /**
     * 通过 错误消息 和 错误码 构造异常对象
     */
    public BusinessException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    /**
     * 通过 三个参数 构造异常对象
     * @param message 错误消息
     * @param code 错误码
     * @param cause 原始异常对象
     */
    public BusinessException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
    }

    /**
     * 通过 响应枚举类 创造异常对象
     * @param resultCodeEnum 接收枚举类型
     */
    public BusinessException(ResponseEnum resultCodeEnum) {
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }

    /**
     * 通过 响应枚举类 和 原始异常对象 构造异常对象
     * @param resultCodeEnum 接收枚举类型
     * @param cause 原始异常对象
     */
    public BusinessException(ResponseEnum resultCodeEnum, Throwable cause) {
        super(cause);
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }
}
