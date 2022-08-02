package com.dry.srb.sms.client;

import com.dry.srb.sms.client.fallback.CoreUserInfoClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 远程调用service-core微服务中的功能
 */
@FeignClient(value = "service-core", fallback = CoreUserInfoClientFallback.class)
public interface CoreUserInfoClient {

    @RequestMapping("/api/core/userInfo/checkMobile/{mobile}")
    boolean checkMobile(@PathVariable String mobile);

}
