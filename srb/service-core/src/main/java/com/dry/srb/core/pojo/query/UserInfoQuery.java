package com.dry.srb.core.pojo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用于查询的pojo，筛选条件对象
 */
@Data
@ApiModel(description = "会员搜索对象")
public class UserInfoQuery {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户状态")
    private Integer status;

    @ApiModelProperty("用户类型：1：出借人 2：借款人")
    private Integer userType;
}
