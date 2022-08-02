package com.dry.mybatisxtest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.mybatisxtest.entity.User;
import com.dry.mybatisxtest.mapper.UserMapper;
import com.dry.mybatisxtest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> listAllByName(String name) {
        List<User> users = userMapper.selectAllByNameUsers(name);
        return users;
    }
}
