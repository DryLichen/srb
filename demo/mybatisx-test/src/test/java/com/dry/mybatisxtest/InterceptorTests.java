package com.dry.mybatisxtest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.mybatisxtest.entity.Product;
import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.mapper.ProductMapper;
import com.dry.mybatisxtest.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class InterceptorTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;

    @Test
    void testSelect(){
        // 1.创建分页参数
        Page<User> pageParam = new Page<>(2, 3);
        // 2.查询
        userMapper.selectPage(pageParam, null);

        List<User> users = pageParam.getRecords();
        users.forEach(System.out :: println);
        // 查看分页参数
        System.out.println(pageParam);

        // 查询总记录数
        Long count = pageParam.getTotal();
        System.out.println(count);
    }

    @Test
    void testCustomize(){
        Page<User> pageParam = new Page<>(2, 3);
        userMapper.selectPageByAge(pageParam, 100);
        List<User> users = pageParam.getRecords();
        users.forEach(System.out :: println);
    }

    @Test
    void testOpLock(){
        // 1.小李
        Product p1 = productMapper.selectById(1L);

        // 2.小王
        Product p2 = productMapper.selectById(1L);

        // 3.小李将价格加了50元，存入了数据库
        p1.setPrice(p1.getPrice() + 50);
        int result1 = productMapper.updateById(p1);
        System.out.println("小李修改结果：" + result1);

        // 4.小王将商品减了30元，存入了数据库
        p2.setPrice(p2.getPrice() - 30);
        int result2 = productMapper.updateById(p2);
        if(result2 == 0){
            p2 = productMapper.selectById(1L);
            result2 = productMapper.updateById(p2);
        }
        System.out.println("小王修改结果：" + result2);

        // 5.最后的结果
        Product p3 = productMapper.selectById(1L);
        System.out.println(p3.getPrice());
    }
}
