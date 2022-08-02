package com.dry.srb.core.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 帮助生成unique编号
 * 编号一般在请求汇付宝时生成，然后汇付宝调用异步接口时传回来，再保存到商户平台流水表中
 * 如果汇付宝没有获取success响应，则尝试再次调用接口
 * 为了保证接口幂等性，如果流水编号已经存在于流水表，说明接口被调用过，不再执行操作
 */
public class LendNoUtils {

    // 通用方法
    public static String getNo() {
        LocalDateTime time=LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String strDate = dtf.format(time);

        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }

        return strDate + result;
    }

    // 充值编号
    public static String getChargeNo() {
        return "CHARGE" + getNo();
    }

    // 生成标的编号
    public static String getLendNo() {
        return "LEND" + getNo();
    }

    // 生成投标编号
    public static String getLendItemNo() {
        return "INVEST" + getNo();
    }

    // 生成放款编号
    public static String getLoanNo() {
        return "LOAN" + getNo();
    }

    // 还款编号
    public static String getReturnNo() {
        return "RETURN" + getNo();
    }

    // 提现标号
    public static Object getWithdrawNo() {
        return "WITHDRAW" + getNo();
    }

    // 回款编号
    public static String getReturnItemNo() {
        return "RETURNITEM" + getNo();
    }

    /**
     * 获取交易编码
     */
    public static String getTransNo() {
        return "TRANS" + getNo();
    }

}