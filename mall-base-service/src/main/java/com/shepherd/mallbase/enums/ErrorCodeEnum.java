package com.shepherd.mallbase.enums;

import lombok.Getter;

/**
 * @author jfWu
 * @version 1.0
 * @date 2019/11/18 11:07
 */
@Getter
public enum ErrorCodeEnum {

    SEND_MESSAGE_ERROR("SEND MESSAGE ERROR", "发送短信失败，请稍后重试"),

    VERIFICATION_PHONE_ERROR("VERIFICATION ERROR", "验证发生错误，请确认是否是当前手机号获取的验证码"),

    VERIFICATION_CODE_ERROR("VERIFICATION CODE ERROR", "验证码不正确或已过期，请重新输入"),

    USER_NO_NOT_EXIST_ERROR("USER NOT EXIST ERROR", "用户不存在，请重新确认再输入"),

    PASSWORD_ERROR("PASSWORD ERROR", "密码错误，请重新输入"),

    PHONE_NOT_REGISTER("PHONE NOT REGISTER", "该手机号尚未注册使用过");

    private String code;
    private String message;

    ErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
