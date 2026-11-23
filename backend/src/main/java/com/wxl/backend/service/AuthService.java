package com.wxl.backend.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wxl.backend.common.AuthErrorCode;
import com.wxl.backend.common.BusinessException;
import com.wxl.backend.dto.RegisterRequest;
import com.wxl.backend.dto.RegisterResponse;
import com.wxl.backend.entity.SysUser;
import com.wxl.backend.entity.UserAuth;
import com.wxl.backend.mapper.SysUserMapper;
import com.wxl.backend.mapper.UserAuthMapper;
import com.wxl.backend.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private static final String AUTH_TYPE_EMAIL_PASSWORD = "email_password";
    private static final String CODE_KEY_PREFIX = "auth:register:code:";
    private static final String ATTEMPT_KEY_PREFIX = "auth:register:attempt:";
    private static final String SEND_LOCK_PREFIX = "auth:register:lock:";
    private static final String IP_SEND_KEY_PREFIX = "auth:register:ip:";

    /** 验证码有效期 */
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    /** 同一邮箱发送间隔 */
    private static final Duration SEND_INTERVAL = Duration.ofSeconds(60);
    /** 单个验证码最多校验次数 */
    private static final int MAX_CODE_ATTEMPTS = 5;
    /** 单 IP 每小时最多发送次数 */
    private static final int MAX_SEND_PER_IP_PER_HOUR = 20;
    private static final Duration IP_WINDOW = Duration.ofHours(1);

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RedisUtil redisUtil;
    private final SysUserMapper sysUserMapper;
    private final UserAuthMapper userAuthMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AuthService(RedisUtil redisUtil,
                       SysUserMapper sysUserMapper,
                       UserAuthMapper userAuthMapper,
                       PasswordEncoder passwordEncoder,
                       MailService mailService) {
        this.redisUtil = redisUtil;
        this.sysUserMapper = sysUserMapper;
        this.userAuthMapper = userAuthMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    /**
     * 发送注册验证码：单 IP 每小时 20 次，同邮箱 60 秒一次，验证码 5 分钟有效。
     */
    public void sendRegisterCode(String rawEmail, String clientIp) {
        String email = normalizeEmail(rawEmail);

        long ipCount = redisUtil.incrementWithTtl(IP_SEND_KEY_PREFIX + clientIp, IP_WINDOW);
        if (ipCount > MAX_SEND_PER_IP_PER_HOUR) {
            throw new BusinessException(AuthErrorCode.IP_SEND_LIMIT_EXCEEDED);
        }

        String lockKey = SEND_LOCK_PREFIX + email;

        if (!redisUtil.setIfAbsent(lockKey, "1", SEND_INTERVAL)) {
            throw new BusinessException(AuthErrorCode.CODE_SEND_TOO_FREQUENT);
        }

        String code = generateCode();
        redisUtil.set(CODE_KEY_PREFIX + email, code, CODE_TTL);
        redisUtil.delete(ATTEMPT_KEY_PREFIX + email);

        try {
            mailService.sendRegisterCode(email, code);
        } catch (Exception ex) {
            // 发送失败：撤销验证码与发送锁，允许立即重试
            redisUtil.delete(CODE_KEY_PREFIX + email);
            redisUtil.delete(lockKey);
            log.error("发送注册验证码失败 email={}", email, ex);
            throw new BusinessException(AuthErrorCode.CODE_SEND_FAILED);
        }
    }

    /**
     * 校验验证码并注册。用户主表与凭证表在同一事务内写入。
     */
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String codeKey = CODE_KEY_PREFIX + email;
        String attemptKey = ATTEMPT_KEY_PREFIX + email;

        String savedCode = redisUtil.get(codeKey);
        if (savedCode == null) {
            throw new BusinessException(AuthErrorCode.CODE_INVALID_OR_EXPIRED);
        }

        long attempts = redisUtil.increment(attemptKey, 1);
        redisUtil.expire(attemptKey, CODE_TTL);
        if (attempts > MAX_CODE_ATTEMPTS) {
            redisUtil.delete(codeKey);
            redisUtil.delete(attemptKey);
            throw new BusinessException(AuthErrorCode.CODE_TOO_MANY_ATTEMPTS);
        }
        if (!savedCode.equals(request.code())) {
            throw new BusinessException(AuthErrorCode.CODE_INVALID_OR_EXPIRED);
        }

        if (isEmailRegistered(email)) {
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        // 验证通过后立即作废，防止重放
        redisUtil.delete(codeKey);
        redisUtil.delete(attemptKey);

        SysUser user = new SysUser();
        user.setNickname(buildNickname(email));
        user.setStatus(1);
        user.setIsDeleted(0);
        sysUserMapper.insert(user);

        UserAuth auth = new UserAuth();
        auth.setUserId(user.getUserId());
        auth.setAuthType(AUTH_TYPE_EMAIL_PASSWORD);
        auth.setAuthValue(email);
        auth.setPassword(passwordEncoder.encode(request.password()));
        auth.setVerified(1);
        try {
            userAuthMapper.insert(auth);
        } catch (DuplicateKeyException ex) {
            // 并发注册同一邮箱时唯一键兜底
            throw new BusinessException(AuthErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        log.info("注册成功 userId={} email={}", user.getUserId(), email);
        return new RegisterResponse(user.getUserId(), user.getNickname(), email);
    }

    private boolean isEmailRegistered(String email) {
        Long count = userAuthMapper.selectCount(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getAuthType, AUTH_TYPE_EMAIL_PASSWORD)
                .eq(UserAuth::getAuthValue, email));
        return count != null && count > 0;
    }

    private String generateCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String buildNickname(String email) {
        String prefix = email.substring(0, email.indexOf('@'));
        return prefix.length() > 64 ? prefix.substring(0, 64) : prefix;
    }
}
