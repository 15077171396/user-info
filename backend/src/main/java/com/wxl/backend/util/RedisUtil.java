package com.wxl.backend.util;

import java.time.Duration;
import java.util.Collection;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 常用操作封装：值统一按 JSON 存取，key 为纯字符串。
 * 计数器（increment）走 StringRedisTemplate，因为 Redis 的 INCR 只能作用于数字字符串。
 */
@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /** 写入且不过期 */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 写入并设置过期时间 */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /** key 不存在时写入（原子），常用于防重复提交 / 发送频率限制 */
    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, ttl));
    }

    /** 读取，类型由调用方决定 */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /** 批量删除，返回实际删除数量 */
    public long delete(Collection<String> keys) {
        Long count = redisTemplate.delete(keys);
        return count == null ? 0L : count;
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /** 重新设置过期时间 */
    public boolean expire(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
    }

    /** 剩余存活秒数：-1 永不过期，-2 key 不存在 */
    public long getExpire(String key) {
        Long seconds = redisTemplate.getExpire(key);
        return seconds == null ? -2L : seconds;
    }

    /** 原子自增，返回自增后的值；key 不存在时从 0 开始 */
    public long increment(String key, long delta) {
        Long value = stringRedisTemplate.opsForValue().increment(key, delta);
        return value == null ? 0L : value;
    }

    /**
     * 固定窗口计数：key 不存在时以 ttl 创建，返回本次自增后的计数。
     * 用于「单位时间内最多 N 次」这类限流。
     */
    public long incrementWithTtl(String key, Duration ttl) {
        stringRedisTemplate.opsForValue().setIfAbsent(key, "0", ttl);
        Long value = stringRedisTemplate.opsForValue().increment(key, 1);
        return value == null ? 0L : value;
    }
}
