package com.dry.mybatisxtest.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

@Data
public class Product {
    @TableId
    private Long id;
    private String name;
    private Integer price;
    @Version
    private Integer version;
}
