package com.wxl.backend.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一响应结构：HTTP 状态码表达请求结果类别，code 表达业务语义。
 */
@Schema(description = "统一响应结构")
public record Result<T>(
        @Schema(description = "业务状态码，200 表示成功", example = "200") int code,
        @Schema(description = "提示信息", example = "成功") String message,
        @Schema(description = "业务数据") T data) {

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static Result<Void> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }
}
