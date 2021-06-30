package com.shepherd.malluser.api.vo;

import lombok.Data;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/29 15:57
 */
@Data
public class LoginResponseVO {
    private Integer isFirstLogin;
    private String token;
    private String accessToken;
    private Long thirdOauthId;
}
