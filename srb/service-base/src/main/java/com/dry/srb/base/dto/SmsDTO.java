package com.dry.srb.base.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "短信")
@Data
public class SmsDTO {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("短信")
    private String message;
}
