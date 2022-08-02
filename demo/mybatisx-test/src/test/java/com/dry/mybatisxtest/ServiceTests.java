package com.dry.mybatisxtest;

import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ServiceTests {

    @Autowired
    private UserService userService;

    @Test
    void testCount(){
        // 计算总记录数
        Long count = userService.count();
        System.out.println("总行数：" + count);
    }

    @Test
    void testSave(){
        List<User> userList = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("Helen" + i);
            user.setAge(10 + i);
            userList.add(user);
        }
        // 添加数据
        boolean flag = userService.saveBatch(userList);
        System.out.println(flag ? "批量添加成功" : "批量添加失败");
    }

    @Test
    void testCustomize(){
        List<User> users = userService.listAllByName("Luda");
        users.forEach(System.out :: println);
    }

    @Test
    void testFill(){
        User user = new User();
        user.setId(1514912952775573505L);
        user.setAge(223);
        boolean flag = userService.updateById(user);
        assertEquals(true, flag);
    }


}
