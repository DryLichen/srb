package com.dry.common.exception;

import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 程序全局异常处理
 */
@Slf4j
@RestControllerAdvice //添加通知，即切入点加入的代码
public class UnifiedExceptionHandler {

    /**
     * 未定义异常的通用处理
     */
    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        log.error(e.getMessage(), e);
        return R.error();
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public R handleBusinessException(BusinessException e){
        log.error(e.getMessage(), e);
        return R.error().code(e.getCode()).message(e.getMessage());
    }

    /**
     * Controller上层异常
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public R handleServletException(Exception e){
        log.error(e.getMessage(), e);
        return R.error().code(ResponseEnum.SERVLET_ERROR.getCode()).message(ResponseEnum.SERVLET_ERROR.getMessage());
    }
}
