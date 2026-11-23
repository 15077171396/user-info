package com.wxl.backend.controller;

import java.util.List;

import com.wxl.backend.common.Result;
import com.wxl.backend.dto.PageResult;
import com.wxl.backend.dto.UserCreateRequest;
import com.wxl.backend.dto.UserPageQuery;
import com.wxl.backend.dto.UserUpdateRequest;
import com.wxl.backend.dto.UserVO;
import com.wxl.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理", description = "后台用户管理接口")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户列表", description = "支持昵称模糊、状态、创建时间区间、用户ID 过滤；邮箱脱敏")
    @GetMapping
    public Result<PageResult<UserVO>> page(UserPageQuery query) {
        return Result.success(userService.page(query));
    }

    @Operation(summary = "查看用户详情", description = "邮箱脱敏，不返回密码")
    @GetMapping("/{userId}")
    public Result<UserVO> detail(@PathVariable Long userId) {
        return Result.success(userService.detail(userId));
    }

    @Operation(summary = "新增用户", description = "默认密码 abc123456，邮箱直接标记为已验证")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.create(request));
    }

    @Operation(summary = "编辑用户", description = "仅可修改昵称、头像、状态")
    @PutMapping("/{userId}")
    public Result<UserVO> update(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return Result.success(userService.update(userId, request));
    }

    @Operation(summary = "批量删除用户", description = "逻辑删除，请求体为用户ID数组")
    @DeleteMapping
    public Result<Void> batchDelete(@RequestBody List<Long> userIds) {
        userService.batchDelete(userIds);
        return Result.success();
    }
}
