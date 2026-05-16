package com.wcinv.shared.exception;

/**
 * 错误码接口，各模块枚举实现此接口。
 */
public interface ErrorCode {
    String getCode();
    String getMessage();
}
