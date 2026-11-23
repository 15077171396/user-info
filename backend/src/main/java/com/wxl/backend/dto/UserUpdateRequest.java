package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "编辑用户请求")
public record UserUpdateRequest(
        @Schema(description = "昵称", example = "张三")
        @NotBlank(message = "昵称不能为空")
        @Size(max = 64, message = "昵称长度不能超过 64 个字符")
        String nickname,

        @Schema(description = "头像URL，传空可清除")
        @Size(max = 255, message = "头像地址长度不能超过 255 个字符")
        String avatar,

        @Schema(description = "状态：1-正常 2-禁用 3-锁定", example = "1")
        @NotNull(message = "状态不能为空")
        @Min(value = 1, message = "状态只能为 1、2、3")
        @Max(value = 3, message = "状态只能为 1、2、3")
        Integer status) {
}
