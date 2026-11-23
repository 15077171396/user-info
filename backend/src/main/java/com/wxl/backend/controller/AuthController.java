package com.wxl.backend.controller;

import com.wxl.backend.common.Result;
import com.wxl.backend.dto.RegisterRequest;
import com.wxl.backend.dto.RegisterResponse;
import com.wxl.backend.dto.SendCodeRequest;
import com.wxl.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证", description = "邮箱注册相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "发送注册验证码", description = "单 IP 每小时 20 次，同一邮箱 60 秒一次，验证码 5 分钟内有效")
    @PostMapping("/register/code")
    public Result<Void> sendRegisterCode(@Valid @RequestBody SendCodeRequest request,
                                         HttpServletRequest httpRequest) {
        authService.sendRegisterCode(request.email(), httpRequest.getRemoteAddr());
        return Result.success();
    }

    @Operation(summary = "邮箱注册", description = "邮箱 + 密码 + 验证码注册，密码 8-16 位且含字母和数字")
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }
}
