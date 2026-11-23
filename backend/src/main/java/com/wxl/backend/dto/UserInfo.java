package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Schema(description = "登录用户信息")
public record UserInfo(
        // 雪花 ID 超出 JS 安全整数范围，序列化成字符串
        @JsonSerialize(using = ToStringSerializer.class)
        @Schema(description = "用户ID", example = "1849234567890123456") Long userId,
        @Schema(description = "昵称", example = "123456") String nickname,
        @Schema(description = "头像URL") String avatar,
        @Schema(description = "邮箱", example = "123456@qq.com") String email) {
}
