package com.dry.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.srb.core.pojo.entity.Borrower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dry.srb.core.pojo.vo.BorrowerApprovalVO;
import com.dry.srb.core.pojo.vo.BorrowerDetailVO;
import com.dry.srb.core.pojo.vo.BorrowerVO;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface BorrowerService extends IService<Borrower> {

    /**
     * 保存借款人信息到尚融宝数据库
     */
    void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId);

    /**
     * 根据用户id获取借款账号认证状态
     */
    Integer getBorrowerStatusByUserId(Long userId);

    /**
     * 获取分页的借款人列表
     */
    IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword);

    /**
     * 根据借款人id获取借款人详细信息
     */
    BorrowerDetailVO getBorrowerDetailVOById(Long id);

    /**
     * 借款额度审批
     */
    void approval(BorrowerApprovalVO borrowerApprovalVO);
}
