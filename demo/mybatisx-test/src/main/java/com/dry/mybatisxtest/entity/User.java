package com.dry.mybatisxtest.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("user")
public class User {

    @TableId(value = "uid", type = IdType.ASSIGN_ID)
    private Long id;

    private String name;
    private Integer age;
    private String email;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer deleted;
}
