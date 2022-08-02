package com.dry.srb.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.dry.common.exception.BusinessException;
import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.core.pojo.dto.ExcelDictDTO;
import com.dry.srb.core.pojo.entity.Dict;
import com.dry.srb.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = "数据字典管理")
@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
// @CrossOrigin
public class AdminDictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("批量导入Excel数据")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public R importBatch(
            @ApiParam(value = "Excel数据字典文件", required = true)
            @RequestPart("file") MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            dictService.importData(inputStream);
            return R.ok().message("批量导入数据成功！");
        } catch (IOException e) {
            // 抛出文件上传错误
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("导出Excel文件")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response){
        try {
            // 最好使用postman测试
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 防止中文乱码
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");

            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet().doWrite(dictService.exportData());

        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.EXPORT_DATA_ERROR, e);
        }
    }

    @ApiOperation("获取数据字典树形表格数据")
    @RequestMapping(value = "/listByParentId/{parentId}", method = RequestMethod.GET)
    public R listByParentId(
            @ApiParam(value = "上级节点Id", required = true)
            @PathVariable("parentId") Long parentId){
        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("dictList", dictList);
    }
}
