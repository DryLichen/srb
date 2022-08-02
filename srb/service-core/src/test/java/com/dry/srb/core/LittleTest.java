package com.dry.srb.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LittleTest {
    @Test
    void test1(){
        Integer a = 1;
        int b = 1;
        System.out.println(a == b);
    }
}
