package com.dry.mybatisxtest;

import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert(){
        User user = new User();
        user.setAge(25);
        user.setName("Luda");

        int count = userMapper.insert(user);
        System.out.println("影响的行数：" + count);
        System.out.println("自动填充的id：" + user.getId());
    }

    @Test
    void testSelect(){
        // 按id查询
        User user = userMapper.selectById(1514883835309019138L);
        System.out.println(user);

        // 按id列表查询
        List<User> userList = userMapper.selectBatchIds(Arrays.asList(1,2,3));
        System.out.println(userList);

        // 按map条件查询
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Billie");
        map.put("age", 24);
        List<User> users = userMapper.selectByMap(map);
        users.forEach(System.out :: println);
    }

    @Test
    void testUpdate(){
        User user = new User();
        user.setId(1514883835309019138L);
        user.setEmail("pleasestay@universe.com");

        int count = userMapper.updateById(user);
        System.out.println("更新的行数：" + count);
    }

    @Test
    void testDelete(){
        // int count = userMapper.deleteById(2);
        Long count = userMapper.selectCount(null);
        System.out.println("影响的行数：" + count);
    }

    @Test
    void testCustomize1(){
        // 根据名称查找用户
        List<User> users = userMapper.selectAllByNameUsers("Luda");
        users.forEach(System.out :: println);
    }

}
