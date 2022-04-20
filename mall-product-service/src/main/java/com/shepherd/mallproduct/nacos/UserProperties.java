package com.shepherd.mallproduct.nacos;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2022/4/20 15:22
 */
@Configuration
@Data
public class UserProperties {
    @Value("${user.name}")
    private String name;
}
