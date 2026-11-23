package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "新增用户请求")
public record UserCreateRequest(
        @Schema(description = "昵称", example = "张三")
        @NotBlank(message = "昵称不能为空")
        @Size(max = 64, message = "昵称长度不能超过 64 个字符")
        String nickname,

        @Schema(description = "邮箱（登录账号）", example = "zhangsan@qq.com")
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱长度不能超过 128 个字符")
        String email,

        @Schema(description = "状态：1-正常 2-禁用 3-锁定，默认 1", example = "1")
        @Min(value = 1, message = "状态只能为 1、2、3")
        @Max(value = 3, message = "状态只能为 1、2、3")
        Integer status) {
}
