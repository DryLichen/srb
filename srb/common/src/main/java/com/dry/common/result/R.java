package com.dry.common.result;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果
 */
@Setter(AccessLevel.PRIVATE)
@Getter
public class R {
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    /**
     * 成功
     */
    public static R ok(){
        R r = new R();
        r.setCode(ResponseEnum.SUCCESS.getCode());
        r.setMessage(ResponseEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 失败
     */
    public static R error(){
        R r = new R();
        r.setCode(ResponseEnum.ERROR.getCode());
        r.setMessage(ResponseEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 自定义结果
     */
    public static R setResult(ResponseEnum responseEnum){
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    // 返回自己R，便于写串联语法
    public R message(String message){
        setMessage(message);
        return this;
    }

    public R code(Integer code){
        setCode(code);
        return this;
    }

    public R data(String key, Object value){
        data.put(key, value);
        return this;
    }

    public R data(Map<String, Object> map){
        setData(map);
        return this;
    }
}
