package com.shepherd.mall;import lombok.Data;import org.springframework.boot.context.properties.ConfigurationProperties;import org.springframework.context.annotation.Configuration;/** * @author bofa1ex * @since 2020/6/15 */@Data@Configuration@ConfigurationProperties(prefix = SwaggerProps.PREFIX)public class SwaggerProps {    public static final String PREFIX = "swagger-web";    private String apiTitle;    private String apiDescription;    private String contactName;    private String contactUrl;    private String contactEmail;    private String scanPackage;}