package com.dry.srb.oss.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.dry.srb.oss.service.OssService;
import com.dry.srb.oss.util.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class OssServiceImpl implements OssService {
    @Override
    public String upload(InputStream inputStream, String module, String fileName) {
        // 连接到远端OSS服务器
        String endpoint = OssProperties.ENDPOINT;
        String accessKeyId = OssProperties.KEY_ID;
        String accessKeySecret = OssProperties.KEY_SECRET;

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 连接到bucket，没有则新建bucket
        String bucketName = OssProperties.BUCKET_NAME;
        if(!ossClient.doesBucketExist(bucketName)){
            ossClient.createBucket(bucketName);
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        //创建文件全路径
        String folder = new DateTime().toString("yyyy/MM/dd");
        fileName = UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
        String objectName = module + "/" + folder + "/" + fileName;

        try {
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 返回阿里云url
        return "https://" + bucketName + "." +
                endpoint.substring(endpoint.lastIndexOf("/") + 1)
                + "/" + objectName;
    }

    @Override
    public void remove(String url) {
        // 连接到远端OSS服务器
        String endpoint = OssProperties.ENDPOINT;
        String accessKeyId = OssProperties.KEY_ID;
        String accessKeySecret = OssProperties.KEY_SECRET;
        String bucketName = OssProperties.BUCKET_NAME;

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        //获取文件全路径
        String host = "https://" + OssProperties.BUCKET_NAME + "." + OssProperties.ENDPOINT;
        String objectName = url.substring(host.length());

        try {
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
