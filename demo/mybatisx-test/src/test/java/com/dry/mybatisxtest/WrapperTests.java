package com.dry.mybatisxtest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class WrapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelect(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "Helen")
                .between("age", 0, 20)
                .isNull("email")
                .orderByAsc("age");

        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out :: println);
    }

    @Test
    void testDelete(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .isNull("email")
                .like("name", "Helen");
        int count = userMapper.delete(queryWrapper);
        log.info("删除的记录数：{}", count);
    }

    /**
     * 组装 select 语句，查询特定的字段
     */
    @Test
    void testSelect2(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "age");

        // selectMaps 返回map列表，配合 select 使用避免列值为null
        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);
        maps.forEach(System.out :: println);
    }

    /**
     * 子查询
     */
    @Test
    void testSub(){}

    /**
     * UpdateWrapper
     */
    @Test
    void testUpdate(){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("age", 18)
                .set("email", "hhhhhhhhhhh")
                .and(i -> i.gt("age", 18).or().isNull("email"));

        User user = new User();
        int result = userMapper.update(user, updateWrapper);
        System.out.println(result);
    }

    /**
     * 测试 boolean condition
     * 查询名字中包含n，年龄大于10且小于20的用户，查询条件来源于用户输入，是可选的
     */
    @Test
    void testCondition(){
        //模拟查询条件，有可能为null（用户未输入）
        String name = null;
        Integer ageBegin = 10;
        Integer ageEnd = 20;

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .like(StringUtils.hasLength(name), "name", name)
                .gt(ageBegin != null, "age", ageBegin)
                .lt(ageEnd != null, "age", ageEnd);

        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }
}
