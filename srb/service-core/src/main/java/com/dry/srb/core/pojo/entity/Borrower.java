package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 借款人
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@ApiModel(value = "Borrower对象", description = "借款人")
public class Borrower implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("性别（1：男 0：女）")
    private Integer sex;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("学历")
    private Integer education;

    @ApiModelProperty("是否结婚（1：是 0：否）")
    @TableField("is_marry")
    private Boolean marry;

    @ApiModelProperty("行业")
    private Integer industry;

    @ApiModelProperty("月收入")
    private Integer income;

    @ApiModelProperty("还款来源")
    private Integer returnSource;

    @ApiModelProperty("联系人名称")
    private String contactsName;

    @ApiModelProperty("联系人手机")
    private String contactsMobile;

    @ApiModelProperty("联系人关系")
    private Integer contactsRelation;

    @ApiModelProperty("状态（0：未认证，1：认证中， 2：认证通过， -1：认证失败）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;
}
