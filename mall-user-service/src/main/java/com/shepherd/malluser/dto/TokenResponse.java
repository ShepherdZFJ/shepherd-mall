package com.shepherd.malluser.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private Long id;
    private String client_id;
}
