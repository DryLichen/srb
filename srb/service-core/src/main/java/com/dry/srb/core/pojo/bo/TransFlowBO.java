package com.dry.srb.core.pojo.bo;

import com.dry.srb.core.enums.TransTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 业务表单，用于服务内部方便传输数据，没有实体对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransFlowBO {

    private String agentBillNo;
    private String bindCode;
    private BigDecimal amount;
    private TransTypeEnum transTypeEnum;
    private String memo;
    
}
