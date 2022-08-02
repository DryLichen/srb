package com.dry.mybatisxtest;

import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisxTestApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelectList() {
        List<User> userList = userMapper.selectList(null);
        userList.forEach(System.out :: println);
    }

}
