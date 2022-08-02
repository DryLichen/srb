package com.dry.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.common.exception.Assert;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.enums.BorrowAuthEnum;
import com.dry.srb.core.enums.BorrowInfoStatusEnum;
import com.dry.srb.core.enums.UserBindEnum;
import com.dry.srb.core.mapper.BorrowerMapper;
import com.dry.srb.core.mapper.IntegralGradeMapper;
import com.dry.srb.core.mapper.UserInfoMapper;
import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.dry.srb.core.mapper.BorrowInfoMapper;
import com.dry.srb.core.pojo.entity.Borrower;
import com.dry.srb.core.pojo.entity.IntegralGrade;
import com.dry.srb.core.pojo.entity.UserInfo;
import com.dry.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.dry.srb.core.pojo.vo.BorrowerDetailVO;
import com.dry.srb.core.service.BorrowInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dry.srb.core.service.BorrowerService;
import com.dry.srb.core.service.DictService;
import com.dry.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Service
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private IntegralGradeMapper integralGradeMapper;
    @Autowired
    private BorrowInfoMapper borrowInfoMapper;
    @Autowired
    private BorrowerMapper borrowerMapper;

    @Autowired
    private DictService dictService;
    @Autowired
    private BorrowerService borrowerService;
    @Autowired
    private LendService lendService;

    /**
     * 获取用户的 借款额度
     */
    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        // 获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        // 根据积分获取借款额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start", integral)
                .ge("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        if(integralGrade == null){
            return new BigDecimal(0);
        }

        BigDecimal borrowAmount = integralGrade.getBorrowAmount();
        return borrowAmount;
    }

    /**
     * 客户端 用户提交 借款申请
     */
    @Transactional( rollbackFor = Exception.class)
    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 1.判断用户是否已绑定汇付宝
        Integer bindStatus = userInfo.getBindStatus();
        Assert.isTrue(bindStatus == UserBindEnum.BIND_OK.getStatus(), ResponseEnum.USER_NO_BIND_ERROR);

        // 2.判断用户借款人认证状态
        Integer borrowAuthStatus = userInfo.getBorrowAuthStatus();
        Assert.isTrue(borrowAuthStatus == BorrowAuthEnum.AUTH_OK.getStatus(), ResponseEnum.USER_NO_AMOUNT_ERROR);

        // 3.判断用户借款金额是否超过额度
        BigDecimal borrowAmount = getBorrowAmount(userId);
        Assert.isTrue(borrowAmount.doubleValue() >= borrowInfo.getAmount().doubleValue(), ResponseEnum.USER_AMOUNT_LESS_ERROR);

        // 4.插入借款记录到数据表
        // 将百分比值转化为小数，除以100
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        borrowInfo.setUserId(userId);
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());

        borrowInfoMapper.insert(borrowInfo);
    }

    /**
     * 获取用户借款申请 审核状态
     */
    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.eq("user_id", userId);
        BorrowInfo borrowInfo = borrowInfoMapper.selectOne(borrowInfoQueryWrapper);
        if(borrowInfo == null){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }

        Integer status = borrowInfo.getStatus();
        return status;
    }

    /**
     * 获取借款申请的 分页列表数据
     */
    @Override
    public List<BorrowInfo> getListPage(Long page, Long size, String keyword) {
        page = (page - 1) * size;
        List<BorrowInfo> borrowInfoList = borrowInfoMapper.selectBorrowInfoList(page, size, keyword);
        // 将需要展示为字符串的参数，获取后放到param中
        borrowInfoList.forEach( borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
        });

        return borrowInfoList;
    }

    /**
     * 获取借款申请的 总条目数
     */
    public Long getTotal(String keyword){
        Long total = borrowInfoMapper.selectBorrowInfoTotal(keyword);
        return total;
    }

    /**
     * 获取借款申请详情
     */
    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {
        // 获取借款申请对象
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValue("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValue("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);

        // 获取借款人对象
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(queryWrapper);
        BorrowerDetailVO borrowerDetailVO = borrowerService.getBorrowerDetailVOById(borrower.getId());

        // 填充map并返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("borrowInfo", borrowInfo);
        map.put("borrower", borrowerDetailVO);
        return map;
    }

    /**
     * 借款申请审批
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        // 更新借款申请状态
        Long id = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = borrowInfoMapper.selectById(id);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        borrowInfoMapper.updateById(borrowInfo);

        // 如果审核通过，创建标的
        if(borrowInfoApprovalVO.getStatus() == BorrowInfoStatusEnum.CHECK_OK.getStatus()){
            lendService.createLend(borrowInfoApprovalVO, borrowInfo);
        }
    }
}
