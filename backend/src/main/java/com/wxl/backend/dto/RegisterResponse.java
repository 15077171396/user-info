package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Schema(description = "注册结果")
public record RegisterResponse(
        // 雪花 ID 超出 JS 的安全整数范围，序列化成字符串避免前端精度丢失
        @JsonSerialize(using = ToStringSerializer.class)
        @Schema(description = "用户ID", example = "1849234567890123456") Long userId,
        @Schema(description = "昵称", example = "123456") String nickname,
        @Schema(description = "邮箱", example = "123456@qq.com") String email) {
}
