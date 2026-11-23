package com.wxl.backend.common;

/**
 * 认证模块业务错误码，从 1000 起，避免与 HTTP 状态码混淆。
 */
public enum AuthErrorCode implements ErrorCode {

    EMAIL_ALREADY_REGISTERED(1001, "该邮箱已注册"),
    CODE_INVALID_OR_EXPIRED(1002, "验证码错误或已过期"),
    CODE_TOO_MANY_ATTEMPTS(1003, "验证码错误次数过多，请重新获取"),
    CODE_SEND_TOO_FREQUENT(1004, "验证码发送过于频繁，请稍后再试"),
    CODE_SEND_FAILED(1005, "验证码发送失败，请稍后重试"),
    IP_SEND_LIMIT_EXCEEDED(1006, "当前网络发送次数过多，请稍后再试");

    private final int code;
    private final String message;

    AuthErrorCode(int code, String message) {
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
