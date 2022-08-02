package com.dry.srb.core.service;

import com.dry.srb.core.pojo.entity.BorrowerAttach;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.BorrowerAttachVO;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    /**
     * 获取借款人上传的附件材料列表
     */
    List<BorrowerAttachVO> getBorrowerAttachVOList(Long borrowerId);

}
