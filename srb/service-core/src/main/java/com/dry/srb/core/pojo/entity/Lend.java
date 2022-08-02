package com.dry.srb.core.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 标的准备表
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Getter
@Setter
@ApiModel(value = "Lend对象", description = "标的准备表")
public class Lend implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("借款用户id")
    private Long userId;

    @ApiModelProperty("借款信息id")
    private Long borrowInfoId;

    @ApiModelProperty("标的编号")
    private String lendNo;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("标的金额")
    private BigDecimal amount;

    @ApiModelProperty("投资期数")
    private Integer period;

    @ApiModelProperty("年化利率")
    private BigDecimal lendYearRate;

    @ApiModelProperty("平台服务费率")
    private BigDecimal serviceRate;

    @ApiModelProperty("还款方式")
    private Integer returnMethod;

    @ApiModelProperty("最低投资金额")
    private BigDecimal lowestAmount;

    @ApiModelProperty("已投金额")
    private BigDecimal investAmount;

    @ApiModelProperty("投资人数")
    private Integer investNum;

    @ApiModelProperty("发布日期")
    private LocalDateTime publishDate;

    @ApiModelProperty("开始日期")
    private LocalDate lendStartDate;

    @ApiModelProperty("结束日期")
    private LocalDate lendEndDate;

    @ApiModelProperty("说明")
    private String lendInfo;

    @ApiModelProperty("平台预期收益")
    private BigDecimal expectAmount;

    @ApiModelProperty("实际收益")
    private BigDecimal realAmount;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("审核时间")
    private LocalDateTime checkTime;

    @ApiModelProperty("审核用户id")
    private Long checkAdminId;

    @ApiModelProperty("放款时间")
    private LocalDateTime paymentTime;

    @ApiModelProperty("放款人id")
    private LocalDateTime paymentAdminId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("逻辑删除(1:已删除，0:未删除)")
    @TableField("is_deleted")
    @TableLogic
    private Boolean deleted;

    // 扩展字段，在数据库中并不存在
    @ApiModelProperty(value = "其他参数")
    @TableField(exist = false)
    private Map<String,Object> param = new HashMap<>();
}
