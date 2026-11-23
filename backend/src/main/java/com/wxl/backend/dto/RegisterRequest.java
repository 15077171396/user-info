package com.wxl.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "邮箱注册请求")
public record RegisterRequest(
        @Schema(description = "邮箱", example = "123456@qq.com")
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        @Size(max = 128, message = "邮箱长度不能超过 128 个字符")
        String email,

        @Schema(description = "密码，8-16 位且同时包含字母和数字", example = "abc12345")
        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)\\S{8,16}$",
                message = "密码需 8-16 位且同时包含字母和数字，不能有空格")
        String password,

        @Schema(description = "6 位数字验证码", example = "123456")
        @NotBlank(message = "验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "验证码为 6 位数字")
        String code) {
}
