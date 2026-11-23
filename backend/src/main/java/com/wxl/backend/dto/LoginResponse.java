package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录结果")
public record LoginResponse(
        @Schema(description = "token 名称，放入请求头", example = "satoken") String tokenName,
        @Schema(description = "token 值") String tokenValue,
        @Schema(description = "有效期（秒）", example = "604800") long expiresIn,
        @Schema(description = "登录用户信息") UserInfo user) {
}
