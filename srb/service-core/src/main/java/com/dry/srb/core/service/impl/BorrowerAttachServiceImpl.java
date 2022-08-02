package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.srb.core.pojo.entity.BorrowerAttach;
import com.dry.srb.core.mapper.BorrowerAttachMapper;
import com.dry.srb.core.pojo.vo.BorrowerAttachVO;
import com.dry.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Autowired
    private BorrowerAttachMapper borrowerAttachMapper;

    // 获取借款人上传的附件材料列表
    @Override
    public List<BorrowerAttachVO> getBorrowerAttachVOList(Long borrowerId) {
        List<BorrowerAttachVO> borrowerAttachVOList = new ArrayList<>();

        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id", borrowerId);
        List<BorrowerAttach> borrowerAttacheList = borrowerAttachMapper.selectList(borrowerAttachQueryWrapper);

        borrowerAttacheList.forEach( borrowerAttach -> {
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            BeanUtils.copyProperties(borrowerAttach, borrowerAttachVO);
            borrowerAttachVOList.add(borrowerAttachVO);
        });

        return borrowerAttachVOList;
    }
}
