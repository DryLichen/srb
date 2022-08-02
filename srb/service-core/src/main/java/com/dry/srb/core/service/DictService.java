package com.dry.srb.core.service;

import com.dry.srb.core.pojo.dto.ExcelDictDTO;
import com.dry.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
public interface DictService extends IService<Dict> {
    /**
     * 导入Excel数据到数据库
     */
    void importData(InputStream inputStream);

    /**
     * 导出数据成excel文件
     */
    List<ExcelDictDTO> exportData();

    /**
     * 根据数据字典id值获取某节点下子节点列表
     */
    List<Dict> listByParentId(Long parentId);

    /**
     * 根据数据字典code值获取某节点下子节点列表
     */
    List<Dict> findByDictCode(String dictCode);

    /**
     * 根据母节点code和本节点value获取本节点名称
     */
    String getNameByParentDictCodeAndValue(String dictCode, Integer value);
}
