package com.dry.srb.core.mapper;

import com.dry.srb.core.pojo.dto.ExcelDictDTO;
import com.dry.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface DictMapper extends BaseMapper<Dict> {
    /**
     * 批量插入数据
     */
    void insertBatch(List<ExcelDictDTO> list);

}
