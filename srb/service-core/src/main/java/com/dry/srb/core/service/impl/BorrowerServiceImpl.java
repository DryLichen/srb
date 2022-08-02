package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dry.srb.core.enums.BorrowAuthEnum;
import com.dry.srb.core.enums.BorrowerStatusEnum;
import com.dry.srb.core.enums.IntegralEnum;
import com.dry.srb.core.mapper.BorrowerAttachMapper;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.mapper.UserIntegralMapper;
import com.dry.srb.core.pojo.entity.Borrower;
import com.dry.srb.core.mapper.BorrowerMapper;
import com.dry.srb.core.pojo.entity.BorrowerAttach;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.pojo.entity.UserIntegral;
import com.dry.srb.core.pojo.vo.BorrowerApprovalVO;
import com.dry.srb.core.pojo.vo.BorrowerAttachVO;
import com.dry.srb.core.pojo.vo.BorrowerDetailVO;
import com.dry.srb.core.pojo.vo.BorrowerVO;
import com.dry.srb.core.service.BorrowerAttachService;
import com.dry.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.service.DictService;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Autowired
    private BorrowerMapper borrowerMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private BorrowerAttachMapper borrowerAttachMapper;
    @Autowired
    private UserIntegralMapper userIntegralMapper;

    @Autowired
    private DictService dictService;
    @Autowired
    private BorrowerAttachService borrowerAttachService;

    /**
     * 保存借款人信息到尚融宝数据库
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 1.保存借款人信息到borrower表
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowAuthEnum.AUTH_RUN.getStatus());
        borrowerMapper.insert(borrower);

        // 2.保存认证材料到Attach表
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        for(BorrowerAttach borrowerAttach : borrowerAttachList){
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        }


        // 3.更新userInfo借款认证状态
        userInfo.setBorrowAuthStatus(BorrowAuthEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据userId获取借款账号状态
     */
    @Override
    public Integer getBorrowerStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objectList = borrowerMapper.selectObjs(borrowerQueryWrapper);

        if(objectList.size() == 0){
            return BorrowAuthEnum.NO_AUTH.getStatus();
        } else {
            Integer status = (Integer) objectList.get(0);
            return status;
        }
    }

    /**
     * 获取分页的借款人列表
     */
    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        Page<Borrower> pageModel = null;

        if(keyword != null){
            QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
            borrowerQueryWrapper.like("name", keyword)
                    .or().like("id_card", keyword)
                    .or().like("mobile", keyword);
            pageModel = borrowerMapper.selectPage(pageParam, borrowerQueryWrapper);
        } else {
            pageModel = borrowerMapper.selectPage(pageParam, null);
        }

        return pageModel;
    }

    /**
     * 根据borrowerId获取借款人详细信息
     */
    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long borrowerId) {
        Borrower borrower = borrowerMapper.selectById(borrowerId);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();

        // 填充借款人详情VO对象部分属性
        BeanUtils.copyProperties(borrower, borrowerDetailVO);
        borrowerDetailVO.setMarry(borrower.getMarry()? "是" : "否");
        borrowerDetailVO.setSex(borrower.getSex() == 1? "是" : "否");

        // 填充数据字典中的那些属性(因为数据库原始表中这些值存为int，而展示表需要String，所以需要转化)
        String education = dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation());
        String industry = dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry());
        String income = dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome());
        String returnSource = dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource());
        String contactsRelation = dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation());

        borrowerDetailVO.setEducation(education);
        borrowerDetailVO.setIndustry(industry);
        borrowerDetailVO.setIncome(income);
        borrowerDetailVO.setReturnSource(returnSource);
        borrowerDetailVO.setContactsRelation(contactsRelation);

        // 填充审批状态（为了将Integer转化为String）
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        // 填充上传附件
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.getBorrowerAttachVOList(borrowerId);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);

        return borrowerDetailVO;
    }

    /**
     * 借款额度审批
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        // 1.获取借款人对象
        Borrower borrower = borrowerMapper.selectById(borrowerApprovalVO.getBorrowerId());

        // 2.更新borrower表的借款认证状态
        borrower.setStatus(borrowerApprovalVO.getStatus());
        borrowerMapper.updateById(borrower);

        // 3.更新user_info表的借款认证状态
        Long userId = borrower.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());

        // 4.插入积分情况到user_integral表（每一个加分项一条记录）
        Integer integral = userInfo.getIntegral();

        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);
        integral += borrowerApprovalVO.getInfoIntegral();

        if(borrowerApprovalVO.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());

            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        if(borrowerApprovalVO.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());

            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        if(borrowerApprovalVO.getIsIdCardOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());

            userIntegralMapper.insert(userIntegral);
            integral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }

        // 5.更新user_info积分
        userInfo.setIntegral(integral);
        userInfoMapper.updateById(userInfo);
    }
}
