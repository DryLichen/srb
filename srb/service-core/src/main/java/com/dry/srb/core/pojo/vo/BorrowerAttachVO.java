package com.dry.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 管理端 展示借款账户详情
 */
@ApiModel("借款人附件资料")
@Data
public class BorrowerAttachVO {

    @ApiModelProperty("图片路径")
    private String imageUrl;

    @ApiModelProperty("图片类型（idCard1：身份证正面，idCard2：身份证反面，house：房产证，car：车）")
    private String imageType;
}
