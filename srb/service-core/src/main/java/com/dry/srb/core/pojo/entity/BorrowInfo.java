package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 借款信息表
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("borrow_info")
@ApiModel(value = "BorrowInfo对象", description = "借款信息表")
public class BorrowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("借款金额")
    private BigDecimal amount;

    @ApiModelProperty("借款期限")
    private Integer period;

    @ApiModelProperty("年化利率")
    private BigDecimal borrowYearRate;

    @ApiModelProperty("还款方式 1-等额本息 2-等额本金 3-每月还息一次还本 4-一次还本")
    private Integer returnMethod;

    @ApiModelProperty("资金用途")
    private Integer moneyUse;

    @ApiModelProperty("状态（0：未提交，1：审核中， 2：审核通过， -1：审核不通过）")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    // 扩展字段，在数据库中并不存在
    @ApiModelProperty("姓名")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty("手机号")
    @TableField(exist = false)
    private String mobile;

    @ApiModelProperty("其他参数")
    @TableField(exist = false)
    private Map<String, Object> param = new HashMap<>();

}
