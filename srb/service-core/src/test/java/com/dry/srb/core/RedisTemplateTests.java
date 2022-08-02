package com.dry.srb.core;

import com.dry.srb.core.mapper.DictMapper;
import com.dry.srb.core.pojo.entity.Dict;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
public class RedisTemplateTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DictMapper dictMapper;

    @Test
    void saveDict(){
        Dict dict = dictMapper.selectById(1);
        // 内存储存键值对，过期时间5分钟
        redisTemplate.opsForValue().set("dict", dict, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("a", 3);
    }

    @Test
    void getDict(){
        Dict dict = (Dict) redisTemplate.opsForValue().get("dict");
        log.info("获取到dict: {}", dict);
        log.info("a: ", redisTemplate.opsForValue().get("a"));
        log.info("b: ", redisTemplate.opsForValue().get("b"));
    }
}
