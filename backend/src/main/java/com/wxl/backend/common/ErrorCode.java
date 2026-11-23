package com.wxl.backend.common;

/**
 * 业务错误码契约。通用码见 {@link ResultCode}，业务专属码按模块实现本接口。
 */
public interface ErrorCode {

    int getCode();

    String getMessage();
}
