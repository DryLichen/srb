package com.dry.srb.oss.service;

import java.io.InputStream;

public interface OssService {

    /**
     * 上传文件到阿里云OSS
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 删除文件
     */
    void remove(String url);
}
