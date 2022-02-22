package com.shepherd.mallbase.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.shepherd.mallbase.api.service.FileService;
import com.shepherd.mallbase.config.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.util.Date;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/12/29 19:12
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Resource
    private OSSClient ossClient;
    @Resource
    private OssProperties ossProperties;

    @Override
    public String uploadFileToOSS(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            byte[] bytes = file.getBytes();
            StringBuilder objectNameBuilder = new StringBuilder();
            objectNameBuilder.append("user/images/").append(fileName);
            ossClient.putObject(ossProperties.getBucketName(), objectNameBuilder.toString(), new ByteArrayInputStream(bytes));
            //生成直链
            Date date = new Date();
            date.setTime(date.getTime() + 100L*365*24*3600*1000);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), objectNameBuilder.toString(), HttpMethod.GET);
            request.setExpiration(date);
            URL signedUrl = ossClient.generatePresignedUrl(request);
            log.info("[生成OSS直链]对象名:{},直链地址:{}",objectNameBuilder.toString(),signedUrl.toString());
            return signedUrl.toString();
        } catch (Exception e) {
            log.error("upload file to oss error", e);
        }

        return null;
    }

    @Override
    public String downloadOSSFile(String fileName) {
        try {
            OSSObject ossObject = ossClient.getObject(ossProperties.getBucketName(), fileName);
            InputStream content = ossObject.getObjectContent();
            if (content != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) break;
                    System.out.println("\n" + line);
                }
                // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作。
                content.close();
            }
        } catch (Exception e) {
            log.error("download oss file error:", e);
        }

        // 读取文件内容
        return null;
    }

    @Override
    public void deleteOSSFile(String fileName) {
        try {
            ossClient.deleteObject(ossProperties.getBucketName(), fileName);
        } catch (Exception e) {
            log.error("delete oss file error:", e);
        }

    }

    public static void main(String[] args) {

    }
}
