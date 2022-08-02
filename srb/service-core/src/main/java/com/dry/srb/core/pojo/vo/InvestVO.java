package com.dry.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("投标表单信息")
@Data
public class InvestVO {

    @ApiModelProperty("标的编号")
    private Long lendId;

    @ApiModelProperty("投资金额")
    private String investAmount;

    @ApiModelProperty("用户id")
    private Long investUserId;

    @ApiModelProperty("姓名")
    private String investName;

}
