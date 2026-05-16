package com.wcinv.application.exception;

import com.wcinv.shared.exception.ErrorCode;

public enum BizErrorCode implements ErrorCode {

    AUTH_FAILED("A0100", "用户名或密码错误"),
    FORBIDDEN("A0300", "无权限访问"),
    VALIDATION_ERROR("A0400", "请求参数校验失败"),
    INTERNAL_ERROR("B0001", "系统内部错误");

    private final String code;
    private final String message;

    BizErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
