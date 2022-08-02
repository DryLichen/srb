package com.dry.mybatisxtest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.mybatisxtest.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> listAllByName(String name);
}
