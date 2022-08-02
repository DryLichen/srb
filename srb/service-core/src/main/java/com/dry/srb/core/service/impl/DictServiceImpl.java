package com.dry.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dry.srb.core.listener.ExcelDictDTOListener;
import com.dry.srb.core.pojo.dto.ExcelDictDTO;
import com.dry.srb.core.pojo.entity.Dict;
import com.dry.srb.core.mapper.DictMapper;
import com.dry.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Slf4j
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 导入excel数据
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void importData(InputStream inputStream) {
        // 手动new Listener, 因为不是spring组件
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(dictMapper)).sheet().doRead();
        log.info("importData finished");
    }

    /**
     * 返回excelwrite需要的数据
     */
    @Override
    public List<ExcelDictDTO> exportData() {
        List<Dict> dictList = dictMapper.selectList(null);
        // 由于需要写出到excel文件中，所以必须写入DTO中
        List<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());

        dictList.forEach( dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            // 复制对象属性
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });

        return excelDictDTOList;
    }

    /**
     * 返回某节点下的数据列表
     */
    @Override
    public List<Dict> listByParentId(Long parentId){
        // 1.尝试从redis中获取数据，减轻数据库负荷
        List<Dict> dictList = null;

        // 2.保证在redis服务器宕机时继续按照原来的流程获取数据
        try {
            dictList = (List<Dict>) redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            if(dictList != null){
                log.info("从redis获取数据字典数据成功");
                return dictList;
            }
        } catch (Exception e) {
            log.error("redis获取数据字典数据失败", ExceptionUtils.getStackTrace(e));
        }

        // 3.或者从数据库查找数据
        dictList = dictMapper.selectList(new QueryWrapper<Dict>().eq("parent_id", parentId));

        // 如果数据有子节点，则设置 hasChildren 为true
        dictList.forEach(dict -> {
            if(hasChildren(dict.getId())){
                dict.setHasChildren(true);
            }
        });

        // 4.将查找到的数据放入redis
        try{
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList);
            log.info("存放数据字典数据到redis成功");
        } catch (Exception e){
            log.error("存放数据字典数据到redis失败", ExceptionUtils.getStackTrace(e));
        }

        return dictList;
    }

    /**
     * 判断某节点是否有子节点
     */
    private boolean hasChildren(Long id){
        Integer count = dictMapper.selectCount(new QueryWrapper<Dict>().eq("parent_id", id)).intValue();
        if(count > 0){
            return true;
        }
        return false;
    }

    /**
     * 根据数据字典的编码值获取某节点下子节点列表
     */
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict dict = dictMapper.selectOne(dictQueryWrapper);

        List<Dict> dictList = listByParentId(dict.getId());
        return dictList;
    }

    /**
     * 根据母节点code和本节点value获取本节点名称
     * 因为不同母节点下value可能一样，需要区分
     */
    @Override
    public String getNameByParentDictCodeAndValue(String dictCode, Integer value) {
        //根据母节点的dict_code获取母节点对象
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict parentDict = dictMapper.selectOne(dictQueryWrapper);
        if(parentDict == null){
            return "";
        }

        //根据母节点id和自己的value获取name
        dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", parentDict.getId())
                .eq("value", value);
        Dict dict = dictMapper.selectOne(dictQueryWrapper);
        if(dict == null){
            return "";
        }

        return dict.getName();
    }

}
