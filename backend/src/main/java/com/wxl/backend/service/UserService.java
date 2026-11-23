package com.wxl.backend.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wxl.backend.common.AuthErrorCode;
import com.wxl.backend.common.BusinessException;
import com.wxl.backend.common.ResultCode;
import com.wxl.backend.common.UserErrorCode;
import com.wxl.backend.dto.PageResult;
import com.wxl.backend.dto.UserCreateRequest;
import com.wxl.backend.dto.UserPageQuery;
import com.wxl.backend.dto.UserUpdateRequest;
import com.wxl.backend.dto.UserVO;
import com.wxl.backend.entity.SysUser;
import com.wxl.backend.entity.UserAuth;
import com.wxl.backend.mapper.SysUserMapper;
import com.wxl.backend.mapper.UserAuthMapper;
import com.wxl.backend.util.DesensitizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class UserService {

    private static final String AUTH_TYPE_EMAIL_PASSWORD = "email_password";
    /** 管理员新增用户时的默认密码 */
    private static final String DEFAULT_PASSWORD = "abc123456";
    /** 分页上限，防止一次拉取过多数据 */
    private static final long MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_STATUS = 1;

    private final SysUserMapper sysUserMapper;
    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(SysUserMapper sysUserMapper, UserAuthMapper userAuthMapper, PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.userAuthMapper = userAuthMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询，支持昵称（模糊）、状态、创建时间区间、用户ID 过滤。
     */
    public PageResult<UserVO> page(UserPageQuery query) {
        long pageNum = Math.max(1, query.getPageNum());
        long pageSize = Math.min(MAX_PAGE_SIZE, Math.max(1, query.getPageSize()));

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(query.getNickname()), SysUser::getNickname, query.getNickname())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .eq(query.getUserId() != null, SysUser::getUserId, query.getUserId())
                .ge(query.getStartTime() != null, SysUser::getCreatedAt, query.getStartTime())
                .le(query.getEndTime() != null, SysUser::getCreatedAt, query.getEndTime())
                .orderByDesc(SysUser::getCreatedAt)
                .orderByDesc(SysUser::getUserId);

        Page<SysUser> page = sysUserMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, String> emailMap = loadEmails(page.getRecords());
        List<UserVO> records = page.getRecords().stream()
                .map(user -> toVO(user, emailMap.get(user.getUserId())))
                .toList();

        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), records);
    }

    public UserVO detail(Long userId) {
        return toVO(requireUser(userId), loadEmail(userId));
    }

    /**
     * 新增用户：默认密码 abc123456，邮箱直接标记为已验证。
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO create(UserCreateRequest request) {
        String email = normalizeEmail(request.email());
        Long exists = userAuthMapper.selectCount(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getAuthValue, email));
        if (exists != null && exists > 0) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        SysUser user = new SysUser();
        user.setNickname(request.nickname().trim());
        user.setStatus(request.status() == null ? DEFAULT_STATUS : request.status());
        user.setIsDeleted(0);
        sysUserMapper.insert(user);

        UserAuth auth = new UserAuth();
        auth.setUserId(user.getUserId());
        auth.setAuthType(AUTH_TYPE_EMAIL_PASSWORD);
        auth.setAuthValue(email);
        auth.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        auth.setVerified(1);
        try {
            userAuthMapper.insert(auth);
        } catch (DuplicateKeyException ex) {
            // 并发创建同一邮箱时唯一键兜底
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        log.info("管理员新增用户 userId={} email={}", user.getUserId(), email);
        return toVO(user, email);
    }

    /**
     * 编辑用户：仅昵称、头像、状态，邮箱与密码不可改。
     */
    @Transactional(rollbackFor = Exception.class)
    public UserVO update(Long userId, UserUpdateRequest request) {
        requireUser(userId);

        SysUser update = new SysUser();
        sysUserMapper.update(update, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getUserId, userId)
                .set(SysUser::getNickname, request.nickname().trim())
                .set(SysUser::getAvatar, request.avatar())
                .set(SysUser::getStatus, request.status()));

        return toVO(requireUser(userId), loadEmail(userId));
    }

    /**
     * 批量删除：逻辑删除用户主表，保留认证凭证（该邮箱不可再注册）。
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择要删除的用户");
        }
        List<Long> distinctIds = userIds.stream().distinct().toList();
        int deleted = sysUserMapper.deleteByIds(distinctIds);
        log.info("批量删除用户 count={} requested={}", deleted, distinctIds.size());
    }

    private SysUser requireUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private Map<Long, String> loadEmails(List<SysUser> users) {
        if (users.isEmpty()) {
            return Map.of();
        }
        List<Long> userIds = users.stream().map(SysUser::getUserId).toList();
        List<UserAuth> auths = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>()
                .in(UserAuth::getUserId, userIds)
                .eq(UserAuth::getAuthType, AUTH_TYPE_EMAIL_PASSWORD));
        return auths.stream().collect(Collectors.toMap(
                UserAuth::getUserId, UserAuth::getAuthValue, (first, second) -> first));
    }

    private String loadEmail(Long userId) {
        UserAuth auth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getUserId, userId)
                .eq(UserAuth::getAuthType, AUTH_TYPE_EMAIL_PASSWORD));
        return auth == null ? null : auth.getAuthValue();
    }

    private UserVO toVO(SysUser user, String email) {
        return new UserVO(
                user.getUserId(),
                user.getNickname(),
                user.getAvatar(),
                user.getStatus(),
                DesensitizeUtil.maskEmail(email),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                user.getLockedUntil());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
