package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户基本信息
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("user_info")
@ApiModel(value = "UserInfo对象", description = "用户基本信息")
public class UserInfo implements Serializable {

    public static final Integer STATUS_NORMAL = 1;
    public static final Integer STATUS_LOCKED = 0;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("1：出借人 2：借款人")
    private Integer userType;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户密码")
    private String password;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("微信用户标识openid")
    private String openid;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("绑定状态（0：未绑定，1：绑定成功 -1：绑定失败）")
    private Integer bindStatus;

    @ApiModelProperty("借款人认证状态（0：未认证 1：认证中 2：认证通过 -1：认证失败）")
    private Integer borrowAuthStatus;

    @ApiModelProperty("绑定账户协议号")
    private String bindCode;

    @ApiModelProperty("用户积分")
    private Integer integral;

    @ApiModelProperty("状态（0：锁定 1：正常）")
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
