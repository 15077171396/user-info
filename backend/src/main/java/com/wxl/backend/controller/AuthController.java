package com.wxl.backend.controller;

import com.wxl.backend.common.Result;
import com.wxl.backend.dto.LoginRequest;
import com.wxl.backend.dto.LoginResponse;
import com.wxl.backend.dto.RegisterRequest;
import com.wxl.backend.dto.RegisterResponse;
import com.wxl.backend.dto.SendCodeRequest;
import com.wxl.backend.dto.UserInfo;
import com.wxl.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "邮箱密码登录", description = "登录成功返回 token，后续请求放在请求头 satoken 中")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @Operation(summary = "退出登录", description = "使当前 token 失效")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "当前登录用户", description = "根据请求头中的 token 返回用户信息")
    @GetMapping("/me")
    public Result<UserInfo> me() {
        return Result.success(authService.currentUser());
    }
}
