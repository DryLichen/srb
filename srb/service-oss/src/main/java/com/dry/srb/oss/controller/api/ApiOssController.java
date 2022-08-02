package com.dry.srb.oss.controller.api;

import com.dry.common.exception.BusinessException;
import com.dry.common.result.R;
import com.dry.common.result.ResponseEnum;
import com.dry.srb.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Api(tags = "阿里云文件管理")
// @CrossOrigin
@RestController
@RequestMapping("/api/oss/file")
public class ApiOssController {

    @Autowired
    private OssService ossService;

    @ApiOperation("上传文件到OSS")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public R upload(@ApiParam("上传的文件") @RequestPart MultipartFile file,
                    @ApiParam("文件所属模块") @RequestParam String module){
        try {
            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            String url = ossService.upload(inputStream, module, fileName);

            return R.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }

    @ApiOperation("删除OSS的文件")
    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public R remove(@ApiParam("删除的文件路径") @RequestParam String url){
        ossService.remove(url);
        return R.ok().message("删除成功");
    }
}
