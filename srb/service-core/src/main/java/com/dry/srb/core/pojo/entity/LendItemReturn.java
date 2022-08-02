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
 * 标的出借回款记录表
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("lend_item_return")
@ApiModel(value = "LendItemReturn对象", description = "标的出借回款记录表")
public class LendItemReturn implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标的还款id")
    private Long lendReturnId;

    @ApiModelProperty("标的项id")
    private Long lendItemId;

    @ApiModelProperty("标的id")
    private Long lendId;

    @ApiModelProperty("出借用户id")
    private Long investUserId;

    @ApiModelProperty("出借金额")
    private BigDecimal investAmount;

    @ApiModelProperty("当前的期数")
    private Integer currentPeriod;

    @ApiModelProperty("年化利率")
    private BigDecimal lendYearRate;

    @ApiModelProperty("还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本")
    private Integer returnMethod;

    @ApiModelProperty("本金")
    private BigDecimal principal;

    @ApiModelProperty("利息")
    private BigDecimal interest;

    @ApiModelProperty("本息")
    private BigDecimal total;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("还款时指定的还款日期")
    private LocalDate returnDate;

    @ApiModelProperty("实际发生的还款时间")
    private LocalDateTime realReturnTime;

    @ApiModelProperty("是否逾期")
    @TableField("is_overdue")
    private Boolean overdue;

    @ApiModelProperty("逾期金额")
    private BigDecimal overdueTotal;

    @ApiModelProperty("状态（0-未归还 1-已归还）")
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
