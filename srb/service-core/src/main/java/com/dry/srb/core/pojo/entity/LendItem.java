package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 标的出借记录表
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("lend_item")
@ApiModel(value = "LendItem对象", description = "标的出借记录表")
public class LendItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("投资编号")
    private String lendItemNo;

    @ApiModelProperty("标的id")
    private Long lendId;

    @ApiModelProperty("投资用户id")
    private Long investUserId;

    @ApiModelProperty("投资人名称")
    private String investName;

    @ApiModelProperty("投资金额")
    private BigDecimal investAmount;

    @ApiModelProperty("年化利率")
    private BigDecimal lendYearRate;

    @ApiModelProperty("投资时间")
    private LocalDateTime investTime;

    @ApiModelProperty("开始日期")
    private LocalDate lendStartDate;

    @ApiModelProperty("结束日期")
    private LocalDate lendEndDate;

    @ApiModelProperty("预期收益")
    private BigDecimal expectAmount;

    @ApiModelProperty("实际收益")
    private BigDecimal realAmount;

    @ApiModelProperty("状态（0：默认 1：已支付 2：已还款）")
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
