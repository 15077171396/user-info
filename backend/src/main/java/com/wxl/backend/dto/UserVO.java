package com.wxl.backend.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Schema(description = "用户信息（邮箱已脱敏）")
public record UserVO(
        @JsonSerialize(using = ToStringSerializer.class)
        @Schema(description = "用户ID", example = "1849234567890123456") Long userId,
        @Schema(description = "昵称", example = "张三") String nickname,
        @Schema(description = "头像URL") String avatar,
        @Schema(description = "状态：1-正常 2-禁用 3-锁定", example = "1") Integer status,
        @Schema(description = "邮箱（脱敏）", example = "zh***@qq.com") String email,
        @Schema(description = "创建时间") LocalDateTime createdAt,
        @Schema(description = "更新时间") LocalDateTime updatedAt,
        @Schema(description = "最后登录时间") LocalDateTime lastLoginAt,
        @Schema(description = "锁定截止时间") LocalDateTime lockedUntil) {
}
