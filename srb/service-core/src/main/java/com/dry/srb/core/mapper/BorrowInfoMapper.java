package com.dry.srb.core.mapper;

import com.dry.srb.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    /**
     * 联合表 查找分页的借款申请列表
     */
    List<BorrowInfo> selectBorrowInfoList(@Param("page") Long page, @Param("size") Long size, @Param("keyword") String keyword);

    /**
     * 获取借款申请总数
     */
    Long selectBorrowInfoTotal(@Param("keyword") String keyword);
}
