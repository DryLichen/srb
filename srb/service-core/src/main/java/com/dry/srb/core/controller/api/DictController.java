package com.dry.srb.core.controller.api;


import com.dry.common.result.R;
import com.dry.srb.core.pojo.entity.Dict;
import com.dry.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 数据字典 前端控制器
 * </p>
 *
 * @author dry
 * @since 2022-04-21
 */
@Api("数据字典接口")
@Slf4j
@RestController
@RequestMapping("/api/core/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("根据dict_code获取某节点下的子节点列表")
    @RequestMapping("/findByDictCode/{dictCode}")
    public R findByDictCode(@ApiParam("字典编码") @PathVariable("dictCode") String dictCode){
        List<Dict> dictList = dictService.findByDictCode(dictCode);
        return R.ok().data("dictList", dictList);
    }
}

