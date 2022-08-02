package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@ApiModel(value = "Dict对象", description = "数据字典")
public class Dict implements Serializable {

    // 如果实体类没有实现序列化，redis存储会报错
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("上级id")
    private Long parentId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("值")
    private Integer value;

    @ApiModelProperty("编码")
    private String dictCode;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("删除标记（0:不可用 1:可用）")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("是否包含子节点")
    @TableField(exist = false) //不存在于数据库中
    private Boolean hasChildren;

}
