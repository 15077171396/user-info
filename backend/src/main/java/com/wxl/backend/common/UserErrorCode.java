package com.wxl.backend.common;

/**
 * 用户管理模块业务错误码，从 2000 起。
 */
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(2001, "用户不存在");

    private final int code;
    private final String message;

    UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
