package com.shepherd.mallbase.config;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author fjZheng
 * @version 1.0
 * @date 2020/12/29 20:00
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun-oss")
@Component()
@Data
public class OssProperties {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private Integer maxUploadSize;
    private Integer maxInMemorySize;

    @Bean
    public OSSClient aliyunOSSClient(){
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }
}
